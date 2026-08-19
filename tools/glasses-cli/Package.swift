// swift-tools-version:5.7
import PackageDescription

let package = Package(
    name: "glasses-cli",
    platforms: [.macOS(.v12)],
    targets: [
        .target(name: "GlassesKit"),
        .executableTarget(
            name: "glassesctl",
            dependencies: ["GlassesKit"]
        ),
    ]
)
