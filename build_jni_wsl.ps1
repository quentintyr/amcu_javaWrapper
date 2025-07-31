// This script builds the JNI shared library using CMake in WSL Ubuntu-20.04
// Place this in your project root as build_jni_wsl.ps1

$wslDistro = "ubuntu-20.04"
$cppDir = "src/main/cpp"
$buildDir = "src/main/cpp/build-jni"

# Create build directory if it doesn't exist
if (!(Test-Path $buildDir)) {
    New-Item -ItemType Directory -Path $buildDir | Out-Null
}

# Run CMake and make in WSL
wsl -d $wslDistro bash -c "cd /mnt/$(echo $PWD | sed 's|:|/|')/$cppDir/build-jni && cmake .. && make"

Write-Host "JNI library build complete. Check $buildDir for libamcu-jni.so."
