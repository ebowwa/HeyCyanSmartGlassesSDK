"""
Setup script for PyPI distribution - excludes proprietary protocol details
"""

from setuptools import setup, find_packages

# Read README for long description
with open("README.md", "r", encoding="utf-8") as fh:
    long_description = fh.read()

setup(
    name="heycyan-glasses-sdk",
    version="1.0.0",
    author="HeyCyan",
    description="SDK for controlling HeyCyan smart glasses via Bluetooth",
    long_description=long_description,
    long_description_content_type="text/markdown",
    url="https://github.com/ebowwa/HeyCyanGlassesSDK",
    packages=find_packages(exclude=["tests*", "examples*", "*_protocol*"]),
    classifiers=[
        "Development Status :: 4 - Beta",
        "Intended Audience :: Developers",
        "Topic :: Software Development :: Libraries :: Python Modules",
        "Topic :: System :: Hardware :: Hardware Drivers",
        "License :: Other/Proprietary License",
        "Programming Language :: Python :: 3",
        "Programming Language :: Python :: 3.7",
        "Programming Language :: Python :: 3.8",
        "Programming Language :: Python :: 3.9",
        "Programming Language :: Python :: 3.10",
        "Programming Language :: Python :: 3.11",
        "Operating System :: OS Independent",
    ],
    python_requires=">=3.7",
    install_requires=[
        "bleak>=0.20.0",
    ],
    package_data={
        "heycyan_sdk": [
            "*.pyc",  # Include compiled protocol
            ".protocol.dat",  # Or encrypted config
        ],
    },
    exclude_package_data={
        "heycyan_sdk": ["_protocol.py"],  # Exclude source
    },
    project_urls={
        "Documentation": "https://github.com/ebowwa/HeyCyanGlassesSDK/tree/main/python",
        "Source": "https://github.com/ebowwa/HeyCyanGlassesSDK",
        "Bug Reports": "https://github.com/ebowwa/HeyCyanGlassesSDK/issues",
    },
)