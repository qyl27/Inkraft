param (
    [string]$SrcRoot = ".\src\test\resources\sources",
    [string]$DestRoot = ".\src\test\resources\data\testmod\inkraft_story"
)

$ErrorActionPreference = "Stop"
$PSNativeCommandUseErrorActionPreference = $false

Write-Host "Sources directory: $SrcRoot"
Write-Host "Destination directory: $DestRoot"

$inklecate = Get-Command inklecate
$stories = @(Get-ChildItem "$SrcRoot\*.ink")
Write-Host "$($stories.Count) stories found."

$successCount = 0
$failureCount = 0

foreach ($story in $stories) {
    $name = $story.BaseName
    $src = "$SrcRoot\$name.ink"
    $dst = "$DestRoot\$name.ink.json"
    & $inklecate.Source -o $dst $src
    $exitCode = $LASTEXITCODE

    if ($exitCode -eq 0) {
        $successCount++
        Write-Host "Compiled $name"
    } else {
        $failureCount++
        Write-Host "Failed to compile $name (exit code $exitCode)" -ForegroundColor Red
    }
}

Write-Host "Compilation summary: $successCount succeeded, $failureCount failed, $($stories.Count) total."
if ($failureCount -gt 0) {
    exit 1
}

exit 0
