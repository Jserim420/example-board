# 게시판 제작

## 사용기술
- Front-End : Bootstrap
- Back-End : Java, Spring Boot-Gradle
- Database : H2 DB, Spring Data JPA
- Dependencies : Thymeleaf

<BR>

## 기능 요구사항
- 회원가입 및 로그인
- 회원 
    - 게시글/댓글 작성/수정/삭제 가능
- 비회원
    - 게시글 열람만 가능, 댓글 작성/수정/삭제 가능
- 게시글
    - 게시글 페이징, 검색, 조회수, 좋아요
- 댓글
    - 댓글 좋아요

<BR>

## 개발 진행사항
- 08/02 
    - 회원가입/로그인 페이지 제작
    - DB 연결 및 테이블 생성
- 08/03
    - 로그인/회원가입 기능
        - 로그인 : 미가입회원, 비밀번호 불일치 예외처리, 로그인 성공 확인
        - 회원가입 : DB insert확인, 중복회원 예외처리, 암호화
    - 게시글(Board) service, repository, controller 생성
- 08/04 
    - 로그인/회원가입
        - 쿠키를 통한 사용자 세션 유지, 사용자 정보 표시
    - 게시글
        - 작성, 목록, 조회 페이지 생성 및 구현
    - 단위테스트
        - 로그인, 회원가입, 게시글
- 08/07
    - 헤더/푸터 수정
    - 메인(게시글 목록) 페이지 생성 및 구현
    - 게시글 조회 : 작성자와 작성자가 아닐때 버튼UI 상이
- 08/08
    - 페이지네이션
    - 게시글 삭제 구현
- 08/09(1차 피드백)
    - Alert창 : 로그인 실패/성공, 회원가입 실패/성공, 댓글 작성 실패/성공
    - 게시글/댓글 : 수정 페이지 생성 및 구현
- 08/10
    - Alert창 : 게시글 작성 성공/실패, 게시글 삭제 confirm/성공, 로그아웃 confrim/성공, 게시글 수정 confrim/성공
    - 댓글 목록 UI 생성, 구현
    - 게시글 : 게시글 조회 시 작성자를 닉네임으로 변경, 좋아요/조회수 기능 추가
- 08/11
    - 페이지 접속 차단 : 비회원이 글쓰기 링크를 통해 직접 접속하는 시도 차단
    - 댓글 : 수정/삭제/비밀번호 일치 구현
    - 게시글 검색 : 검색범위 설정 및 Pagination
    - 단위 테스트 : 댓글 작성/수정/삭제 테스트, 게시글 검색 테스트
- 08/14
    - 통합 테스트
- 08/16(2차 피드백)
    - 통합 테스트 및 오류수정
- 08/17
    - 댓글 
        - 작성시 비밀번호 암호화
        - 댓글 비밀번호 확인/수정 창을 prompt->popup 변경
        - 사이트 내 XSS 취약점 발견 및 수정
- 08/18
    - ORM(JPA) 사용
        - JPA 라이브러리 설정
        - USER 서비스 전체 수정
        - BOARD 서비스 : 게시글 작성/페이징 수정
- 08/21
    - 조회수는 브라우저 당 한번만 적용
    - Spring Data JPA
        - USER/BOARD/COMMENT
            - Service/Repository/Controller 수정
- 08/22
    - 게시글 및 댓글
        - 좋아요/조회수는 브라우당 한번만 반영(유지시간 24시간)
        - 게시글 및 댓글 수정/삭제를 URL을 통해 직접 접속하려고 할때 접속자 확인하여 확인 차단
    - 페이징 : 오류 수정 및 검색 보완(JPA)
- 08/23
    - 통합테스트

<BR>

## 피드백

### 1차피드백
- 코드의 통일성 유지 : insert는 A를 쓰는데, update는 왜 B를 쓰는가
- 로그인 유지 쿠키 VS 세션


### 2차피드백
- 좋아요 및 조회수는 한사람만 한번씩만 할 수 있도록 변경
- 댓글 작성 비밀번호 암호화 필요
- XSS 취약점 파악 후 수정
- preparedStatement 대신에 ORM사용
- 게시글 및 댓글 수정/삭제는 FRONT 뿐아니라 BACK에서도 차단할 수 있게