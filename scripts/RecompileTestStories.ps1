param (
    [string]$SrcRoot = ".\src\test\resources\sources",
    [string]$DestRoot = ".\src\test\resources\data\testmod\inkraft_story",
    [string[]]$Exclude = @("engine_functions_include.ink")
)

$ErrorActionPreference = "Stop"
$PSNativeCommandUseErrorActionPreference = $false

Write-Host "Sources directory: $SrcRoot"
Write-Host "Destination directory: $DestRoot"

$inklecate = Get-Command inklecate
$srcRootPath = (Resolve-Path -LiteralPath $SrcRoot).Path
$destRootPath = [System.IO.Path]::GetFullPath($DestRoot)
$stories = @(Get-ChildItem -LiteralPath $srcRootPath -Recurse -File -Filter "*.ink" -Exclude $Exclude)
Write-Host "$($stories.Count) stories found."

$successCount = 0
$failureCount = 0

foreach ($story in $stories) {
    $relativePath = [System.IO.Path]::GetRelativePath($srcRootPath, $story.FullName)
    $dst = Join-Path $destRootPath "$relativePath.json"
    $dstDirectory = Split-Path -Parent $dst
    New-Item -ItemType Directory -Path $dstDirectory -Force | Out-Null

    & $inklecate.Source -o $dst $story.FullName
    $exitCode = $LASTEXITCODE

    if ($exitCode -eq 0) {
        $successCount++
        Write-Host "Compiled $relativePath"
    } else {
        $failureCount++
        Write-Host "Failed to compile $relativePath (exit code $exitCode)" -ForegroundColor Red
    }
}

Write-Host "Compilation summary: $successCount succeeded, $failureCount failed, $($stories.Count) total."
if ($failureCount -gt 0) {
    exit 1
}

exit 0
