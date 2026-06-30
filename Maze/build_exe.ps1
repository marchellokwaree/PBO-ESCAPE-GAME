# ============================================
# Optimized Build EXE Script for PBO Escape Game
# Creates a fast-launching native .exe with
# minimal custom JRE runtime
# ============================================

$ErrorActionPreference = "Stop"

$ProjectRoot = $PSScriptRoot
$SrcDir      = Join-Path $ProjectRoot "src"
$BuildDir    = Join-Path $ProjectRoot "build_exe"
$JarDir      = Join-Path $BuildDir "jar"
$ExeOutput   = Join-Path $ProjectRoot "MazeGame-exe"
$JarName     = "MazeGame.jar"
$MainClass   = "Main.App"
$AppName     = "MazeGame"

# Path to JDK 25 tools
$JDK_BIN = "C:\Kuliah Jason\SEMESTER 2\jdk-25_windows-x64_bin\jdk-25.0.1\bin"
$JAVAC    = Join-Path $JDK_BIN "javac.exe"
$JAR      = Join-Path $JDK_BIN "jar.exe"
$JLINK    = Join-Path $JDK_BIN "jlink.exe"
$JPACKAGE = Join-Path $JDK_BIN "jpackage.exe"

Write-Host "================================================" -ForegroundColor Cyan
Write-Host "  Building PBO Escape Game (.EXE) - Optimized" -ForegroundColor Cyan
Write-Host "================================================" -ForegroundColor Cyan
Write-Host ""

# --- Step 1: Clean previous build ---
Write-Host "[1/6] Cleaning previous build..." -ForegroundColor Yellow
if (Test-Path $BuildDir) {
    Remove-Item -Recurse -Force $BuildDir
}
if (Test-Path $ExeOutput) {
    Remove-Item -Recurse -Force $ExeOutput
}
New-Item -ItemType Directory -Path $BuildDir | Out-Null
New-Item -ItemType Directory -Path $JarDir | Out-Null

$ClassesDir = Join-Path $BuildDir "classes"
New-Item -ItemType Directory -Path $ClassesDir | Out-Null
Write-Host "  Done." -ForegroundColor Green

# --- Step 2: Compile all Java source files ---
Write-Host "[2/6] Compiling Java sources with JDK 25..." -ForegroundColor Yellow
$javaFiles = Get-ChildItem -Path $SrcDir -Filter "*.java" -Recurse | ForEach-Object { $_.FullName }

if ($javaFiles.Count -eq 0) {
    Write-Host "  ERROR: No .java files found in $SrcDir" -ForegroundColor Red
    exit 1
}

Write-Host "  Found $($javaFiles.Count) Java source files."
$javaFilesArgs = $javaFiles | ForEach-Object { $_ }

& $JAVAC -d $ClassesDir -sourcepath $SrcDir $javaFilesArgs

if ($LASTEXITCODE -ne 0) {
    Write-Host "  ERROR: Compilation failed!" -ForegroundColor Red
    exit 1
}
Write-Host "  Compilation successful." -ForegroundColor Green

# --- Step 3: Copy resource/asset files ---
Write-Host "[3/6] Copying resource files..." -ForegroundColor Yellow

$AssetsSource = Join-Path $SrcDir "Assets"
$AssetsDest   = Join-Path $ClassesDir "Assets"

if (Test-Path $AssetsSource) {
    Copy-Item -Recurse -Force -Path $AssetsSource -Destination $AssetsDest
    Write-Host "  Assets copied." -ForegroundColor Green
} else {
    Write-Host "  WARNING: No Assets directory found." -ForegroundColor Yellow
}

# --- Step 4: Create JAR file ---
Write-Host "[4/6] Creating JAR file..." -ForegroundColor Yellow

$ManifestDir = Join-Path $BuildDir "manifest"
New-Item -ItemType Directory -Path $ManifestDir | Out-Null

$ManifestFile = Join-Path $ManifestDir "MANIFEST.MF"
Set-Content -Path $ManifestFile -Value "Manifest-Version: 1.0`nMain-Class: $MainClass`n" -NoNewline -Encoding ASCII

$JarOutput = Join-Path $JarDir $JarName

Push-Location $ClassesDir
& $JAR cfm $JarOutput $ManifestFile *
$jarExitCode = $LASTEXITCODE
Pop-Location

if ($jarExitCode -ne 0) {
    Write-Host "  ERROR: JAR creation failed!" -ForegroundColor Red
    exit 1
}
Write-Host "  JAR created." -ForegroundColor Green

# Update root jar as well
$RootJarPath = Join-Path $ProjectRoot $JarName
Copy-Item -Force -Path $JarOutput -Destination $RootJarPath
Write-Host "  Root JAR updated." -ForegroundColor Green

# --- Step 5: Create minimal custom JRE with jlink ---
Write-Host "[5/6] Creating minimal custom JRE (jlink)..." -ForegroundColor Yellow
Write-Host "  Only bundling: java.base, java.desktop" -ForegroundColor DarkYellow

$CustomRuntime = Join-Path $BuildDir "custom-runtime"

& $JLINK `
    --add-modules java.base,java.desktop `
    --strip-debug `
    --no-man-pages `
    --no-header-files `
    --compress zip-6 `
    --output $CustomRuntime

if ($LASTEXITCODE -ne 0) {
    Write-Host "  ERROR: jlink failed!" -ForegroundColor Red
    exit 1
}

$runtimeSize = (Get-ChildItem -Path $CustomRuntime -Recurse | Measure-Object -Property Length -Sum).Sum
Write-Host "  Custom JRE created: $([math]::Round($runtimeSize / 1MB, 2)) MB" -ForegroundColor Green

# --- Step 6: Create native .exe with jpackage using custom runtime ---
Write-Host "[6/6] Creating native .exe with jpackage..." -ForegroundColor Yellow

& $JPACKAGE `
    --type app-image `
    --name $AppName `
    --input $JarDir `
    --main-jar $JarName `
    --main-class $MainClass `
    --dest $ExeOutput `
    --runtime-image $CustomRuntime `
    --app-version "1.0.0" `
    --vendor "PBO Escape Game Team" `
    --description "PBO Escape Game - Maze Adventure" `
    --java-options "-Xms512m" `
    --java-options "-Xmx2048m" `
    --java-options "-XX:+UseSerialGC" `
    --java-options "-XX:TieredStopAtLevel=1"

if ($LASTEXITCODE -ne 0) {
    Write-Host "  ERROR: jpackage failed!" -ForegroundColor Red
    exit 1
}

Write-Host "  Native .exe created!" -ForegroundColor Green

# Cleanup temp build files
Remove-Item -Recurse -Force $ManifestDir -ErrorAction SilentlyContinue

# Show final stats
$exePath = Join-Path $ExeOutput "$AppName\$AppName.exe"
$totalSize = (Get-ChildItem -Path "$ExeOutput\$AppName" -Recurse | Measure-Object -Property Length -Sum).Sum

Write-Host ""
Write-Host "================================================" -ForegroundColor Cyan
Write-Host "  Build Complete!" -ForegroundColor Cyan
Write-Host "================================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "  EXE: $exePath" -ForegroundColor Green
Write-Host "  Total size: $([math]::Round($totalSize / 1MB, 2)) MB" -ForegroundColor White
Write-Host ""
Write-Host "  Optimizations applied:" -ForegroundColor White
Write-Host "    - Minimal JRE (java.base + java.desktop only)" -ForegroundColor DarkYellow
Write-Host "    - Compressed runtime (zip-6)" -ForegroundColor DarkYellow
Write-Host "    - Stripped debug symbols" -ForegroundColor DarkYellow
Write-Host "    - SerialGC (less overhead)" -ForegroundColor DarkYellow
Write-Host "    - TieredStopAtLevel=1 (fast JIT warmup)" -ForegroundColor DarkYellow
Write-Host ""
Write-Host "  Double-click .exe to run!" -ForegroundColor White
Write-Host "================================================" -ForegroundColor Cyan
