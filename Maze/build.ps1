# ============================================
# Build Script for PBO Escape Game (Maze)
# Compiles all Java source files, copies
# resources, and packages into a runnable JAR
# ============================================

$ErrorActionPreference = "Stop"

$ProjectRoot = $PSScriptRoot
$SrcDir      = Join-Path $ProjectRoot "src"
$BuildDir    = Join-Path $ProjectRoot "build"
$JarName     = "MazeGame.jar"
$JarOutput   = Join-Path $ProjectRoot $JarName
$MainClass   = "Main.App"

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Building PBO Escape Game (Maze)..." -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# --- Step 1: Clean previous build ---
Write-Host "[1/4] Cleaning previous build..." -ForegroundColor Yellow
if (Test-Path $BuildDir) {
    Remove-Item -Recurse -Force $BuildDir
}
New-Item -ItemType Directory -Path $BuildDir | Out-Null
Write-Host "  Done." -ForegroundColor Green

# --- Step 2: Compile all Java source files ---
Write-Host "[2/4] Compiling Java sources..." -ForegroundColor Yellow
$javaFiles = Get-ChildItem -Path $SrcDir -Filter "*.java" -Recurse | ForEach-Object { $_.FullName }

if ($javaFiles.Count -eq 0) {
    Write-Host "  ERROR: No .java files found in $SrcDir" -ForegroundColor Red
    exit 1
}

Write-Host "  Found $($javaFiles.Count) Java source files."

# Compile directly using the file paths (avoid @argfile encoding issues)
# Using --release 8 for compatibility with Java 8 runtime
$javaFilesArgs = $javaFiles | ForEach-Object { $_ }

& javac --release 8 -d $BuildDir -sourcepath $SrcDir $javaFilesArgs

if ($LASTEXITCODE -ne 0) {
    Write-Host "  ERROR: Compilation failed!" -ForegroundColor Red
    exit 1
}
Write-Host "  Compilation successful." -ForegroundColor Green

# --- Step 3: Copy resource/asset files ---
Write-Host "[3/4] Copying resource files..." -ForegroundColor Yellow

$AssetsSource = Join-Path $SrcDir "Assets"
$AssetsDest   = Join-Path $BuildDir "Assets"

if (Test-Path $AssetsSource) {
    Copy-Item -Recurse -Force -Path $AssetsSource -Destination $AssetsDest
    Write-Host "  Assets copied." -ForegroundColor Green
} else {
    Write-Host "  WARNING: No Assets directory found at $AssetsSource" -ForegroundColor Yellow
}

# --- Step 4: Create JAR file ---
Write-Host "[4/4] Creating JAR file..." -ForegroundColor Yellow

# Create manifest
$ManifestDir = Join-Path $ProjectRoot "manifest"
if (Test-Path $ManifestDir) {
    Remove-Item -Recurse -Force $ManifestDir
}
New-Item -ItemType Directory -Path $ManifestDir | Out-Null

$ManifestFile = Join-Path $ManifestDir "MANIFEST.MF"
# Manifest must end with a newline
Set-Content -Path $ManifestFile -Value "Manifest-Version: 1.0`nMain-Class: $MainClass`n" -NoNewline -Encoding ASCII

# Build the jar from the build directory
Push-Location $BuildDir
jar cfm $JarOutput $ManifestFile *
$jarExitCode = $LASTEXITCODE
Pop-Location

# Cleanup manifest
Remove-Item -Recurse -Force $ManifestDir -ErrorAction SilentlyContinue

if ($jarExitCode -ne 0) {
    Write-Host "  ERROR: JAR creation failed!" -ForegroundColor Red
    exit 1
}

Write-Host "  JAR created successfully." -ForegroundColor Green

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Build Complete!" -ForegroundColor Cyan
Write-Host "  Output: $JarOutput" -ForegroundColor Cyan
Write-Host "" -ForegroundColor Cyan
Write-Host "  Run with: java -jar $JarName" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
