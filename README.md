# Team_Kiosk

오라클 XE를 이용하여 Java와 DB연결

DBConnector클래스에서 주석을 확인하여 계정과 비밀번호 수정 후 Save.

관리자 SQL문 = 관리자 모드 실행에 필요한 SQL문
k_member 관련 SQL문 = 로그인 및 회원가입에 필요한 SQL문
product 관련 SQL문 = product관련 명령에 필요한 SQL문

관리자 모드 관련 SQL문

-------관리자모드 table 생성-------
CREATE TABLE admin (
    id VARCHAR(50) NOT NULL,
    password VARCHAR(50) NOT NULL,
    PRIMARY KEY (id)
);
-------관리자모드 계정 생성-------
INSERT INTO admin (id, password) VALUES ('admin', '12345');


===========================================================


-------k_member table 생성-------
CREATE TABLE k_member (
    id VARCHAR2(50) PRIMARY KEY,
    password VARCHAR2(50),
    cash NUMBER(10,2)
);

-------k_member table 실험계정 추가-------
insert into k_member values ('kiosk', 12345, 30000);


===========================================================


product 관련 SQL문
-------product table 생성-------
CREATE TABLE product (
    product_id NUMBER PRIMARY KEY,
    product_name VARCHAR2(50),
    price NUMBER(10,2),
    quantity NUMBER
);
-------product table 물품 추가-------
INSERT INTO product(product_id, product_name, price, quantity)
VALUES(1, '펩카콜라', 1200, 50);

INSERT INTO product(product_id, product_name, price, quantity)
VALUES(2, '코시', 1000, 50);

INSERT INTO product(product_id, product_name, price, quantity)
VALUES(3, '사이다', 1200, 10);

INSERT INTO product(product_id, product_name, price, quantity)
VALUES(4, '양말', 3000, 20);

INSERT INTO product(product_id, product_name, price, quantity)
VALUES(5, '면도기', 8000, 10);

INSERT INTO product(product_id, product_name, price, quantity)
VALUES(6, '빨래세제', 15000, 10);

INSERT INTO product(product_id, product_name, price, quantity)
VALUES(7, '샴푸', 12000, 10);
