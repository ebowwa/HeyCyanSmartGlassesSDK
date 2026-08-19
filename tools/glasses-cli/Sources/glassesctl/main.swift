import Foundation
import GlassesKit

// glassesctl — a small command-line driver for HeyCyan / A02S ("M02S") glasses
// over Bluetooth LE. Built on GlassesKit; runs on macOS.

let args = Array(CommandLine.arguments.dropFirst())
let command = args.first ?? "help"
let rest = Array(args.dropFirst())

func optValue(_ name: String) -> String? {
    guard let i = rest.firstIndex(of: name), i + 1 < rest.count else { return nil }
    return rest[i + 1]
}

func usage() {
    print("""
    glassesctl — control HeyCyan / A02S smart glasses over BLE

    USAGE: glassesctl <command> [options]

    Status
      info                 Firmware / hardware / Wi-Fi firmware / serial
      battery              Battery percent and charging state
      count                Stored media counts (photos / videos / audio)
      status               info + battery + count in one shot

    Capture
      photo                Take a photo (then re-reads the count to confirm)
      video-start          Start video recording
      video-stop           Stop video recording
      record-start         Start audio recording
      record-stop          Stop audio recording
      aiphoto [--save P]   Trigger an AI photo, try to save its BLE thumbnail

    Point-and-ask
      ask ["question"]     Take an AI photo, send it to a vision model, speak the answer
                           (needs ANTHROPIC_API_KEY; default question: "What am I looking at?")

    Media / transfer
      thumb [i] [--save P] Request thumbnail for index i (default 0)
      wifi                 Enter Wi-Fi transfer mode, print the glasses' P2P IP

    Audio
      audio-on             Enable the classic-Bluetooth (A2DP) audio radio
      audio-off            Disable it

    Notes
      • Turn off Bluetooth on your phone first, or it holds the connection.
      • Full-res media only transfers over Wi-Fi Direct (phone-side), not here.
    """)
}

func done(_ code: Int32) {
    // Cleanly drop the link so the glasses re-advertise promptly for the next
    // invocation, then exit.
    g.disconnect()
    DispatchQueue.main.asyncAfter(deadline: .now() + 0.4) { exit(code) }
}

func fail(_ message: String) {
    FileHandle.standardError.write(Data(("error: " + message + "\n").utf8))
    done(1)
}

if command == "help" || command == "-h" || command == "--help" {
    usage(); exit(0)
}

let g = GlassesClient()

func printInfo(_ then: @escaping () -> Void) {
    g.deviceInfo { r in
        switch r {
        case .success(let i):
            print("Device:       \(g.deviceName)")
            if !i.serial.isEmpty { print("Serial:       \(i.serial)") }
            print("Firmware:     \(i.firmware)")
            print("Hardware:     \(i.hardware)")
            print("Wi-Fi FW:     \(i.wifiFirmware)")
            print("Wi-Fi HW:     \(i.wifiHardware)")
        case .failure(let e): print("info: \(e)")
        }
        then()
    }
}

func printBattery(_ then: @escaping () -> Void) {
    g.battery { r in
        switch r {
        case .success(let b): print("Battery:      \(b.percent)%\(b.charging ? " (charging)" : "")")
        case .failure(let e): print("battery: \(e)")
        }
        then()
    }
}

func printCount(_ then: @escaping () -> Void) {
    g.mediaCount { r in
        switch r {
        case .success(let m): print("Media:        \(m.images) photos, \(m.videos) videos, \(m.audio) audio (\(m.total) total)")
        case .failure(let e): print("count: \(e)")
        }
        then()
    }
}

g.connect { result in
    if case .failure(let e) = result { fail("\(e)"); return }

    switch command {
    case "info":    printInfo { done(0) }
    case "battery": printBattery { done(0) }
    case "count":   printCount { done(0) }
    case "status":  printInfo { printBattery { printCount { done(0) } } }

    case "photo":
        g.setMode(.photo) { r in
            if case .failure(let e) = r { fail("\(e)"); return }
            print("Shutter triggered; confirming…")
            DispatchQueue.main.asyncAfter(deadline: .now() + 2.0) {
                printCount { done(0) }
            }
        }
    case "video-start":  g.setMode(.videoStart)  { _ in print("video recording started");  done(0) }
    case "video-stop":   g.setMode(.videoStop)   { _ in print("video recording stopped");  done(0) }
    case "record-start": g.setMode(.audioStart)  { _ in print("audio recording started");  done(0) }
    case "record-stop":  g.setMode(.audioStop)   { _ in print("audio recording stopped");  done(0) }

    case "aiphoto":
        print("Triggering AI photo; waiting for it to be ready…")
        g.captureAIPhoto { r in
            switch r {
            case .success(let d):
                let path = optValue("--save") ?? "aiphoto.jpg"
                try? d.write(to: URL(fileURLWithPath: path))
                print("saved photo: \(path) (\(d.count) bytes)")
            case .failure(let e):
                print("aiphoto: \(e)")
            }
            done(0)
        }

    case "ask":
        // First non-flag argument (if any) is the question.
        let question = rest.first(where: { !$0.hasPrefix("--") }) ?? "What am I looking at? Answer in one or two sentences."
        print("📸 Taking a photo through the glasses…")
        g.captureAIPhoto { r in
            guard case .success(let jpeg) = r else {
                if case .failure(let e) = r { fail("capture failed: \(e)") }
                return
            }
            let path = optValue("--save") ?? "ask.jpg"
            try? jpeg.write(to: URL(fileURLWithPath: path))
            print("   captured \(jpeg.count) bytes → \(path)")

            guard VisionClient.credentialsFromEnvironment() != nil else {
                print("\n(Photo saved. Set ANTHROPIC_API_KEY to have the glasses answer questions about it.)")
                done(0); return
            }
            print("🧠 Asking: \(question)")
            switch VisionClient.describe(jpeg: jpeg, prompt: question) {
            case .success(let answer):
                print("\n💬 \(answer)\n")
                // Speak it (routes to the glasses if they're the current audio output).
                let p = Process()
                p.executableURL = URL(fileURLWithPath: "/usr/bin/say")
                p.arguments = [answer]
                try? p.run(); p.waitUntilExit()
            case .failure(let e):
                print("vision: \(e)")
            }
            done(0)
        }

    case "thumb":
        let idx = Int(rest.first(where: { Int($0) != nil }) ?? "0") ?? 0
        g.thumbnail(index: idx, timeout: 10) { data in
            if let d = data {
                let path = optValue("--save") ?? "thumb_\(idx).jpg"
                try? d.write(to: URL(fileURLWithPath: path))
                print("saved: \(path) (\(d.count) bytes)")
            } else {
                print("no JPEG returned for index \(idx) (stored gallery is served over Wi-Fi, not BLE)")
            }
            done(0)
        }

    case "wifi":
        print("entering transfer mode…")
        g.enterTransfer { r in
            switch r {
            case .success(let ip): print("glasses P2P IP: \(ip)  (files at http://\(ip)/files/… over Wi-Fi Direct)")
            case .failure(let e):  print("wifi: \(e)")
            }
            done(0)
        }

    case "audio-on":  g.setAudioBluetooth(on: true)  { _ in print("audio radio enabled (pair/connect 'M02S…' as a Bluetooth speaker)"); done(0) }
    case "audio-off": g.setAudioBluetooth(on: false) { _ in print("audio radio disabled"); done(0) }

    default:
        fail("unknown command '\(command)' (try: glassesctl help)")
    }
}

RunLoop.main.run()
