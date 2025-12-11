# System Instructions

## Default

- Be sure to answer in Korean.
- Don't add unnecessary comments.
- Consider maintenance.
- Write it concisely considering code readability
- Prohibits direct inclusion of code-sensitive information.

## Git Commit Message

- The git commit message in Korean.
- Include labels in the first line, such as feat, refactor, fix, etc.
- The first line is concisely summarized to no more than 20 characters.
- Empty the second line and concisely organize the modifications from the third line to no more than three lines.

## Backend

- Must be use `gradle`
- Spring Boot + JDK 17+

## Frontend

- Must be use `pnpm`
- Make `modern web` frontend structure 
- Project Configuration: Quasar + Vite + TailwindCSS
- Use `axios` for API
- Use `pinia` for State Management
- Use `svg` as much as possible for the image

## DevOps

- Consider it configured as an `AWS infrastructure`.
- The backend application runs as `AWS ECS`.
- The frontend application is distributed to `cloudfront`.
- Operational log management uses `loki`.
- Use sentry for error tracking