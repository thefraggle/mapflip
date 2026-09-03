# Contributing to MapFlip

Thank you for your interest in contributing to MapFlip!

## Local Development

1. Clone the repository:
   ```bash
   git clone https://github.com/thefraggle/mapflip.git
   cd mapflip
   ```
2. Build the FOSS debug APK:
   ```bash
   ./gradlew assembleFossDebug
   ```
3. Run unit tests:
   ```bash
   ./gradlew test
   ```

## Pull Request Guidelines

- **Branch naming:** `feature/your-feature` or `fix/your-fix`.
- **Commit messages:** Follow [Conventional Commits](https://www.conventionalcommits.org/) (e.g., `feat: ...`, `fix: ...`, `docs: ...`).
- Ensure all tests pass locally before opening a pull request.
- Add or update unit tests for any new parser or feature.

## Translations

MapFlip supports 19 languages. Translations are managed in `app/src/main/java/de/goork/mapflip/ui/Strings.kt`. Corrections and new language additions are always welcome!
