# glasses-cli — GlassesKit + `glassesctl`

A small, dependency-free **Bluetooth-LE control layer** for the HeyCyan /
oudmon `A02S` glasses (they advertise as `M02S…`), plus a CLI that exercises it.
This is the reusable Apple-side control layer for building an app on these
glasses — the Android side mirrors it via the vendor `glasses_sdk_*.aar`.

Everything here was reverse-engineered from the vendor SDK and **verified
against real hardware** (device info, battery, media count, and photo capture
confirmed by the on-device counter incrementing).

## Build & run

```bash
cd tools/glasses-cli
swift build
.build/debug/glassesctl status
```

> Turn **Bluetooth off on your phone first** — a phone holds the BLE connection
> and the glasses stop advertising while connected.

## Commands

| Command | What it does |
|---|---|
| `status` | firmware + battery + media count in one connect |
| `info` | main firmware/hardware + separate Wi-Fi-chip firmware + serial |
| `battery` | battery percent + charging flag |
| `count` | stored photo / video / audio counts |
| `photo` | take a photo (re-reads the count to confirm) |
| `video-start` / `video-stop` | video recording |
| `record-start` / `record-stop` | audio recording |
| `aiphoto [--save P]` | trigger an AI photo and save the JPEG (pulled over BLE) |
| `ask ["question"] [--save P]` | **point-and-ask**: photo → vision model → spoken answer |
| `thumb [i] [--save P]` | request a thumbnail for index `i` |
| `wifi` | enter Wi-Fi transfer mode, print the glasses' P2P IP |
| `audio-on` / `audio-off` | toggle the classic-Bluetooth (A2DP) audio radio |

Example:

```
$ glassesctl status
Device:       M02S_F00B
Firmware:     A02S_1.00.17_251030
Hardware:     A02S_V1.0
Wi-Fi FW:     WIFIA02S_3.00.02_2512011430A01
Wi-Fi HW:     WIFIA02S_V1.0
Battery:      100%
Media:        4 photos, 0 videos, 1 audio (5 total)
```

## Point-and-ask (`ask`)

The flagship demo. One command:

```bash
export ANTHROPIC_API_KEY=sk-ant-...      # the "brain" for the vision step
glassesctl ask "What am I looking at?"
```

What happens, all over Bluetooth:

1. **Trigger an AI photo** (`glassesControl` work-mode 6).
2. **Wait for the "photo ready" event** the glasses push (device-report action `0x73`, subtype `0x02`).
3. **Pull the JPEG** — it arrives in ~11 chunks over the bigData channel (each frame's header carries `total`/`index`; `ask` requests each chunk and concatenates the bytes from offset 11). Result: a real ~384×288 JPEG.
4. **Ask a vision model** — the JPEG + your question go to the Anthropic Messages API (`claude-opus-5`).
5. **Speak the answer** through the glasses' speaker (via `say`, which follows the Mac's current audio output — pair the glasses' audio first with `audio-on` to route it there).

Without a key it still captures and saves the photo, then tells you to set `ANTHROPIC_API_KEY`. This is the one app direction that needs **no Wi-Fi** — it rides entirely on BLE + audio, so it works from the desktop.

## Architecture

`GlassesKit` (library) is UI-agnostic and reusable in an iOS app:

- **`Protocol.swift`** — service/characteristic UUIDs, frame builders, the
  SDK's CRC16, work-mode enums, and decoded result types. Two channels:
  - *UART small-command* (`6e40fff0…`): fixed 16-byte `[opcode, payload…, checksum]`.
    Used for the audio-radio toggle.
  - *bigData / serial* (`de5bf728…`): `[0xBC, action, len(LE16), crc16(LE16), payload]`,
    reassembled across notifications. Carries device info, battery, media
    control and thumbnails.
- **`GlassesClient.swift`** — a `CBCentralManager` wrapper with a callback API
  (`connect`, `deviceInfo`, `battery`, `mediaCount`, `setMode`, `captureAIPhoto`,
  `enterTransfer`, `setAudioBluetooth`, `thumbnail`). Drive it from a run loop.
  `captureAIPhoto` handles the trigger → ready-event → chunked-JPEG sequence;
  set `GLASSES_DEBUG=1` to trace bigData frames on stderr.
- **`VisionClient.swift`** — a small Anthropic Messages API client (`claude-opus-5`,
  base64 image block) used by `ask`. Auth from `ANTHROPIC_API_KEY` /
  `ANTHROPIC_AUTH_TOKEN`; handles `stop_reason: "refusal"`.

`glassesctl` is a thin CLI over that library.

## Known constraints (by design of the glasses)

- **Full-res media only transfers over Wi-Fi Direct** (P2P, group-owner IP from
  `wifi`), which macOS cannot join — that path is phone-side (Android
  `WifiP2pManager` / iOS `NEHotspotConfiguration`). This tool covers everything
  reachable over BLE.
- **BLE thumbnails** are only pushed for freshly-captured AI photos, not the
  stored gallery (`thumb` returns nothing for stored media — that's expected).
- The **audio-radio toggle** is write-acked but sends no notification; success
  is the write completing.
