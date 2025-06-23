# PowerShell script to help remove Lombok annotations

Write-Host "This script will help identify Lombok usage patterns to remove" -ForegroundColor Yellow

$files = Get-ChildItem -Path "src" -Filter "*.java" -Recurse

foreach ($file in $files) {
    $content = Get-Content $file.FullName -Raw
    $modified = $false
    
    # Remove Lombok imports
    if ($content -match "import lombok\.") {
        Write-Host "File with Lombok imports: $($file.Name)" -ForegroundColor Cyan
        $content = $content -replace "import lombok\..*?;\r?\n", ""
        $modified = $true
    }
    
    # Replace @Slf4j with Logger
    if ($content -match "@Slf4j") {
        $className = [System.IO.Path]::GetFileNameWithoutExtension($file.Name)
        $loggerDeclaration = "    private static final Logger log = LoggerFactory.getLogger($className.class);"
        
        # Add import if not present
        if ($content -notmatch "import org\.slf4j\.Logger;") {
            $content = $content -replace "(package .*?;\r?\n)", "`$1`nimport org.slf4j.Logger;`nimport org.slf4j.LoggerFactory;`n"
        }
        
        # Remove @Slf4j and add logger after class declaration
        $content = $content -replace "@Slf4j\r?\n", ""
        $content = $content -replace "(public class $className[^{]*{)", "`$1`n$loggerDeclaration`n"
        
        Write-Host "Replaced @Slf4j in: $($file.Name)" -ForegroundColor Green
        $modified = $true
    }
    
    # Remove other Lombok annotations (for manual handling)
    $lombokAnnotations = @("@Data", "@RequiredArgsConstructor", "@Builder", "@NoArgsConstructor", "@AllArgsConstructor", "@Getter", "@Setter")
    foreach ($annotation in $lombokAnnotations) {
        if ($content -match [regex]::Escape($annotation)) {
            Write-Host "Found $annotation in: $($file.Name) - needs manual update" -ForegroundColor Yellow
            $content = $content -replace "$annotation\r?\n", ""
            $modified = $true
        }
    }
    
    if ($modified) {
        Set-Content -Path $file.FullName -Value $content -NoNewline
    }
}

Write-Host "`nLombok annotations have been removed." -ForegroundColor Green
Write-Host "Files with @RequiredArgsConstructor need manual constructor creation." -ForegroundColor Yellow
Write-Host "Files with @Data need getters/setters added." -ForegroundColor Yellow