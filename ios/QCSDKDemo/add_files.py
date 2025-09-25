#!/usr/bin/env python3

import re
import sys
import uuid

def add_files_to_pbxproj(pbxproj_path):
    # Read the current pbxproj file
    with open(pbxproj_path, 'r') as f:
        content = f.read()

    # Generate unique IDs for the new files
    media_h_id = "BB" + str(uuid.uuid4()).upper()[:8].replace("-", "")
    media_m_id = "BB" + str(uuid.uuid4()).upper()[:8].replace("-", "")
    media_build_id = "BB" + str(uuid.uuid4()).upper()[:8].replace("-", "")

    # Find the PBXBuildFile section and add our new file
    build_file_pattern = r'(\/\* Begin PBXBuildFile section \*\/\n)'
    replacement = r'\1\t\t' + media_build_id + ' /* MediaGalleryViewController.m in Sources */ = {isa = PBXBuildFile; fileRef = ' + media_m_id + ' /* MediaGalleryViewController.m */; };\n'
    content = re.sub(build_file_pattern, replacement, content)

    # Find the PBXFileReference section and add our files
    file_ref_pattern = r'(AA9999A12E40000000B03938 \/\* GlassesMediaDownloader\.m \*\/ = \{isa = PBXFileReference; lastKnownFileType = sourcecode\.c\.objc; path = GlassesMediaDownloader\.m; sourceTree = \"<group>\"; \};)'
    replacement = r'\1\n\t\t' + media_h_id + ' /* MediaGalleryViewController.h */ = {isa = PBXFileReference; lastKnownFileType = sourcecode.c.h; path = MediaGalleryViewController.h; sourceTree = "<group>"; };\n\t\t' + media_m_id + ' /* MediaGalleryViewController.m */ = {isa = PBXFileReference; lastKnownFileType = sourcecode.c.objc; path = MediaGalleryViewController.m; sourceTree = "<group>"; };'
    content = re.sub(file_ref_pattern, replacement, content)

    # Find the QCSDKDemo group section and add our files
    group_pattern = r'(AA9999A12E40000000B03938 \/\* GlassesMediaDownloader\.m \*\/,\n\t\t\t\tAA1313BA2E2F9E9800B03938 \/\* QCCentralManager\.h \*\/,)'
    replacement = r'AA9999A12E40000000B03938 /* GlassesMediaDownloader.m */,\n\t\t\t\t' + media_h_id + ' /* MediaGalleryViewController.h */,\n\t\t\t\t' + media_m_id + ' /* MediaGalleryViewController.m */,\n\t\t\t\tAA1313BA2E2F9E9800B03938 /* QCCentralManager.h */,'
    content = re.sub(group_pattern, replacement, content)

    # Find the PBXSourcesBuildPhase section and add our file to compilation
    sources_pattern = r'(AA9999A22E40000000B03938 \/\* GlassesMediaDownloader\.m in Sources \*\/,\n\t\t\t\t)'
    replacement = r'AA9999A22E40000000B03938 /* GlassesMediaDownloader.m in Sources */,\n\t\t\t\t' + media_build_id + ' /* MediaGalleryViewController.m in Sources */,\n\t\t\t\t'
    content = re.sub(sources_pattern, replacement, content)

    # Write the modified content back
    with open(pbxproj_path, 'w') as f:
        f.write(content)

    print(f"Added MediaGalleryViewController files to project with IDs: {media_h_id}, {media_m_id}, {media_build_id}")

if __name__ == "__main__":
    pbxproj_path = "../QCSDKDemo.xcodeproj/project.pbxproj"
    add_files_to_pbxproj(pbxproj_path)