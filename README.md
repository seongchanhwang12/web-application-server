# 실습을 위한 개발 환경 세팅
* https://github.com/slipp/web-application-server 프로젝트를 자신의 계정으로 Fork한다. Github 우측 상단의 Fork 버튼을 클릭하면 자신의 계정으로 Fork된다.
* Fork한 프로젝트를 eclipse 또는 터미널에서 clone 한다.
* Fork한 프로젝트를 eclipse로 import한 후에 Maven 빌드 도구를 활용해 eclipse 프로젝트로 변환한다.(mvn eclipse:clean eclipse:eclipse)
* 빌드가 성공하면 반드시 refresh(fn + f5)를 실행해야 한다.

# 웹 서버 시작 및 테스트
* webserver.WebServer 는 사용자의 요청을 받아 RequestHandler에 작업을 위임하는 클래스이다.
* 사용자 요청에 대한 모든 처리는 RequestHandler 클래스의 run() 메서드가 담당한다.
* WebServer를 실행한 후 브라우저에서 http://localhost:8080으로 접속해 "Hello World" 메시지가 출력되는지 확인한다.

# 각 요구사항별 학습 내용 정리
* 구현 단계에서는 각 요구사항을 구현하는데 집중한다. 
* 구현을 완료한 후 구현 과정에서 새롭게 알게된 내용, 궁금한 내용을 기록한다.
* 각 요구사항을 구현하는 것이 중요한 것이 아니라 구현 과정을 통해 학습한 내용을 인식하는 것이 배움에 중요하다. 

### 요구사항 1 - http://localhost:8080/index.html로 접속시 응답
* 새롭게 알게된 점 
 - WebServer의 동작 원리. tomcat과 같은 was 없이 어플리케이션을 서버에 처음 배포해보면서 배포에
 - IDE없이 터미널 만으로 maven 빌드를 처음 해보면서, mvn 명령어 입력 시 일어나는 동작에 대해 알게됐다.
 - 터미널을 이용해 java -cp 옵션을 사용하면 target 디렉토리를 지정해 dependcy 아래에 있는 의존성 라이브러리들과 컴파일 된 클래스들을 클래스 패스에 추가하여 실행 할 수 있다.
 - ssh key를 만들어 등록하면 github 원격 레파지토리와 통신할 때 인증 절차를 거치지 않아도 된다.

* 궁금한 내용 
 - 모호했던 jar와 war파일의 배포 방식과 특징에 대해서도 추가적으로 공부했다.

오늘 학습한 내용과 궁금했던 내용들에 대해 학습하고 정리한 [노션 페이지](https://chan9301.notion.site/http-localhost-8080-index-html-161285ded9708039a25bd1eb17f2cf00?pvs=74) 


### 요구사항 2 - get 방식으로 회원가입
* 

### 요구사항 3 - post 방식으로 회원가입
* 

### 요구사항 4 - redirect 방식으로 이동
* 

### 요구사항 5 - cookie
* 

### 요구사항 6 - stylesheet 적용
* 

### heroku 서버에 배포 후
* 
