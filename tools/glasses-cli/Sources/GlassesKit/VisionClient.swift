import Foundation

/// Minimal Anthropic Messages API client for the "point-and-ask" flow: send a
/// JPEG plus a question and get back a one-line answer.
///
/// Auth resolves from the environment, matching the Anthropic SDKs:
///   * `ANTHROPIC_API_KEY`  → `x-api-key` header
///   * `ANTHROPIC_AUTH_TOKEN` → `Authorization: Bearer` + the oauth beta header
public enum VisionClient {
    public struct Credentials {
        let header: (String, String)
        let extra: [(String, String)]
    }

    public static func credentialsFromEnvironment() -> Credentials? {
        let env = ProcessInfo.processInfo.environment
        if let key = env["ANTHROPIC_API_KEY"], !key.isEmpty {
            return Credentials(header: ("x-api-key", key), extra: [])
        }
        if let tok = env["ANTHROPIC_AUTH_TOKEN"], !tok.isEmpty {
            return Credentials(header: ("Authorization", "Bearer \(tok)"),
                               extra: [("anthropic-beta", "oauth-2025-04-20")])
        }
        return nil
    }

    public enum VisionError: Error, CustomStringConvertible {
        case noCredentials
        case http(Int, String)
        case refused(String)
        case badResponse(String)
        public var description: String {
            switch self {
            case .noCredentials: return "no ANTHROPIC_API_KEY / ANTHROPIC_AUTH_TOKEN in environment"
            case .http(let c, let b): return "HTTP \(c): \(b.prefix(300))"
            case .refused(let c): return "model refused (\(c))"
            case .badResponse(let s): return "unexpected response: \(s.prefix(300))"
            }
        }
    }

    /// Blocks until the API responds. Model defaults to claude-opus-5.
    public static func describe(jpeg: Data, prompt: String,
                                model: String = "claude-opus-5",
                                maxTokens: Int = 300) -> Result<String, Error> {
        guard let creds = credentialsFromEnvironment() else { return .failure(VisionError.noCredentials) }

        let body: [String: Any] = [
            "model": model,
            "max_tokens": maxTokens,
            "messages": [[
                "role": "user",
                "content": [
                    ["type": "image",
                     "source": ["type": "base64", "media_type": "image/jpeg",
                                "data": jpeg.base64EncodedString()]],
                    ["type": "text", "text": prompt],
                ],
            ]],
        ]

        var req = URLRequest(url: URL(string: "https://api.anthropic.com/v1/messages")!)
        req.httpMethod = "POST"
        req.setValue("application/json", forHTTPHeaderField: "Content-Type")
        req.setValue("2023-06-01", forHTTPHeaderField: "anthropic-version")
        req.setValue(creds.header.1, forHTTPHeaderField: creds.header.0)
        for (k, v) in creds.extra { req.setValue(v, forHTTPHeaderField: k) }
        req.httpBody = try? JSONSerialization.data(withJSONObject: body)
        req.timeoutInterval = 60

        var out: Result<String, Error> = .failure(VisionError.badResponse("no response"))
        let sem = DispatchSemaphore(value: 0)
        URLSession.shared.dataTask(with: req) { data, resp, err in
            defer { sem.signal() }
            if let err = err { out = .failure(err); return }
            let code = (resp as? HTTPURLResponse)?.statusCode ?? 0
            let data = data ?? Data()
            guard code == 200 else { out = .failure(VisionError.http(code, String(decoding: data, as: UTF8.self))); return }
            guard let json = try? JSONSerialization.jsonObject(with: data) as? [String: Any] else {
                out = .failure(VisionError.badResponse(String(decoding: data, as: UTF8.self))); return
            }
            if (json["stop_reason"] as? String) == "refusal" {
                let cat = (json["stop_details"] as? [String: Any])?["category"] as? String ?? "unspecified"
                out = .failure(VisionError.refused(cat)); return
            }
            let blocks = json["content"] as? [[String: Any]] ?? []
            let text = blocks.compactMap { $0["type"] as? String == "text" ? $0["text"] as? String : nil }
                .joined(separator: " ")
            out = text.isEmpty ? .failure(VisionError.badResponse(String(decoding: data, as: UTF8.self)))
                               : .success(text.trimmingCharacters(in: .whitespacesAndNewlines))
        }.resume()
        sem.wait()
        return out
    }
}
