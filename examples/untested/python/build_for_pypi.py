#!/usr/bin/env python3
"""
Build script to prepare HeyCyan SDK for PyPI distribution
This compiles/obfuscates the protocol details
"""

import os
import sys
import shutil
import py_compile
import tempfile
from pathlib import Path

def obfuscate_protocol():
    """
    Compile protocol module to bytecode to hide implementation
    """
    print("Obfuscating protocol details...")
    
    # Path to protocol file
    protocol_file = Path("heycyan_sdk/_protocol.py")
    
    if not protocol_file.exists():
        print("Warning: Protocol file not found")
        return
    
    # Compile to bytecode
    compiled_file = Path("heycyan_sdk/_protocol.pyc")
    py_compile.compile(str(protocol_file), str(compiled_file))
    
    # Remove source file from distribution
    protocol_file.unlink()
    
    print("Protocol obfuscated")

def create_encrypted_config():
    """
    Alternative: Store protocol details in encrypted config
    """
    import json
    import base64
    from cryptography.fernet import Fernet
    
    # Generate key (in production, this would be stored securely)
    key = Fernet.generate_key()
    cipher = Fernet(key)
    
    # Protocol configuration
    protocol_config = {
        "services": {
            "primary": "7905FFF0-B5CE-4E99-A40F-4B1E122D00D0",
            "secondary": "6e40fff0-b5a3-f393-e0a9-e50e24dcca9e"
        },
        "characteristics": {
            "command": "6e40fff1-b5a3-f393-e0a9-e50e24dcca9e",
            "notify": "6e40fff2-b5a3-f393-e0a9-e50e24dcca9e",
            "data": "6e40fff3-b5a3-f393-e0a9-e50e24dcca9e"
        },
        "commands": {
            "photo": "020101",
            "video_start": "020102",
            "video_stop": "020103",
            "audio_start": "020108",
            "audio_stop": "02010C"
        }
    }
    
    # Encrypt configuration
    encrypted = cipher.encrypt(json.dumps(protocol_config).encode())
    
    # Save encrypted config
    config_file = Path("heycyan_sdk/.protocol.dat")
    config_file.write_bytes(encrypted)
    
    # Save key separately (would be handled differently in production)
    key_file = Path("heycyan_sdk/.key")
    key_file.write_bytes(key)
    
    print("Protocol configuration encrypted")

def use_cython_compilation():
    """
    Alternative: Use Cython to compile to C extension
    """
    try:
        from Cython.Build import cythonize
        from distutils.core import setup
        from distutils.extension import Extension
        
        # Create .pyx file from .py
        shutil.copy("heycyan_sdk/_protocol.py", "heycyan_sdk/_protocol.pyx")
        
        # Compile to C extension
        ext_modules = cythonize("heycyan_sdk/_protocol.pyx", 
                               compiler_directives={'language_level': "3"})
        
        # Remove source files
        Path("heycyan_sdk/_protocol.py").unlink()
        Path("heycyan_sdk/_protocol.pyx").unlink()
        
        return ext_modules
        
    except ImportError:
        print("Cython not available, using bytecode compilation")
        return None

def prepare_distribution():
    """
    Prepare the package for PyPI distribution
    """
    print("Preparing HeyCyan SDK for PyPI distribution...")
    
    # Create distribution directory
    dist_dir = Path("dist_build")
    if dist_dir.exists():
        shutil.rmtree(dist_dir)
    
    # Copy package files
    shutil.copytree("heycyan_sdk", dist_dir / "heycyan_sdk", 
                    ignore=shutil.ignore_patterns("_protocol.py", "*.pyc", "__pycache__"))
    
    # Copy other necessary files
    shutil.copy("setup.py", dist_dir / "setup.py")
    shutil.copy("README.md", dist_dir / "README.md")
    shutil.copy("requirements.txt", dist_dir / "requirements.txt")
    
    # Choose obfuscation method
    os.chdir(dist_dir)
    
    # Method 1: Bytecode compilation
    obfuscate_protocol()
    
    # Method 2: Encrypted configuration (alternative)
    # create_encrypted_config()
    
    # Method 3: Cython compilation (if available)
    # ext_modules = use_cython_compilation()
    
    print("Distribution prepared in dist_build/")
    print("\nTo upload to PyPI:")
    print("1. cd dist_build")
    print("2. python setup.py sdist bdist_wheel")
    print("3. twine upload dist/*")

if __name__ == "__main__":
    prepare_distribution()