## 스와이프 쇼핑 <스쇼> Core Server Repository

### 연관 Repository
- ssho-shopping-mall-crawling-server(크롤링 서버) : https://github.com/boogle-lab/ssho-shopping-mall-crawling-server
- ssho-logcollector-server(로그 수집 서버) : https://github.com/boogle-lab/ssho-logcollector-server
- ssho-prototype(클라이언트) : https://github.com/boogle-lab/ssho-prototype

### 서버 아키텍쳐
<img src="https://user-images.githubusercontent.com/23696493/86536733-ddd9fc80-bf24-11ea-801a-2347716a69bb.png" width="600" height="auto">	

### 추천 전략
- **Matrix Factorization 기반의 Collaborative Filtering**을 이용하게 됨  
- **추천 상품 업데이트 로직은 API가 아닌 배치**로 구현이 되며 배치 작업 완료 후 추천 상품 저장소 (redis)에 저장함
- 추천 상품 조회 API는 모델 서빙 서버는 거치지 않고 바로 core 서버 -> 추천 상품 저장소 -> core 서버의 flow로 데이터 파이프라인을 구성 (**캐싱 전략**을 활용한다고 볼 수 있음)

### 도메인

- User(회원)
- Item(상품)
- Swipe(스와이프 로그)

### 사용 데이터 저장소(RDB/NoSQL)
- User -> MySQL(RDB)
  - 데이터 정합성 유지가 필요한 회원 정보에 적합한 RDB 선택
- Item -> MongoDB(NoSQL)
  - 적재 위주(크롤링 상품 업데이트)의 작업을 수행하기에 적합
    - Read & Write 성능 우수
    - 대량의 데이터 write 위주의 크롤링 상품 업데이트 작업 수행에 적합
    - Auto-Sharding 지원
  - Schema-less
    - 상품 데이터 구조의 변경에 유연하게 적응 가능
  - 데이터 필터링 작업에 필요한 기능 제공
    - Full-Index 지원
    - 다양한 종류의 쿼리문 지원(필터링, 수집, 정렬, 정규표현식 등)
- Swipe -> Elasticsearch(NoSQL / Distributed Search Engine)
  - 대량의 비정형 데이터 보관 및 검색 가능
    - 기존 데이터베이스로 처리하기 어려운 대량의 비정형 데이터 검색이 가능
  - Kibana를 통한 로그 분석에 용이
  - Schema-less
    - 로그 데이터 구조의 변경에 유연하게 적응 가능

### ERD / 도메인 구조

 <img src="https://user-images.githubusercontent.com/23696493/86262482-e5369880-bbfa-11ea-82a2-1621b62bdab5.png" width="400" height="auto">			
 <img src="https://user-images.githubusercontent.com/23696493/86262664-2464e980-bbfb-11ea-9dbf-a96f79cd29eb.png" width="400" height="auto">
 <img src="https://user-images.githubusercontent.com/23696493/86262702-321a6f00-bbfb-11ea-8b76-d502197a7451.png" width="400" height="auto">
