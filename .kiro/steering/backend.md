---
inclusion: always
---
<!------------------------------------------------------------------------------------
   Add rules to this file or a short description and have Kiro refine them for you.
   
   Learn about inclusion modes: https://kiro.dev/docs/steering/#inclusion-modes
-------------------------------------------------------------------------------------> 

백엔드 애플리케이션을 직접 실행하지 마세요.

Google Gen AI Java SDK 의존성은 `com.google.genai:google-genai` 입니다.

OpenAI Java Library 의존성은 `com.openai:openai-java-spring-boot-starter` 입니다.

사용자의 허락없이 의존성 또는 버전을 변경하지 마십시오.

Getter와 Setter를 작성하지 말고 Lombok 라이브러리를 활용하십시오.

Application Properties 는 final 필드와 함께 생성자 바인딩으로 구성하고 @Getter를 추가합니다.