create table if not exists users(
	user_id varchar(128) not null primary key,
	username varchar(32) not null comment "유저 아이디",
	password varchar(256) not null comment "유저 패스워드",
	constraint uq_users unique (username)
);

create table if not exists clients(
 	client_id	varchar(128) primary key,
	user_id varchar(128) not null comment "유저 아이디",
	response_types varchar(128) not null comment "허용된 grant 유형",
	scopes varchar(128) not null comment "허용된 권한(범위)들",
	redirect_uris varchar(256) not null comment "허용된 리다이렉션 uri들",
	constraint fk_client_user foreign key (user_id) references users (user_id)
);

create table if not exists access_tokens(
	client_id varchar(128) not null comment "유저가 로그인한 클라이언트 아이디",
	username varchar(128) not null comment "로그인한 유저 이름. 아이디 아님.",
	access_token blob comment "액세스 토큰",
	refresh_token blob comment "리프레시 토큰",
	authentication blob comment "client_id, client_credential을 검사하기 위한 authentication 정보",
	expires_at timestamp comment "만료시간, 등록후 한시간 후로 추가",
   constraint fk_access_token_user foreign key (username) references users (username),
	constraint fk_access_token_client foreign key (client_id) references clients (client_id)
);


create table if not exists codes(
	code varchar(128) primary key,
	authentication blob comment "client_id, client_credential을 검사하기 위한 authentication 정보",
	redirect_uri varchar(256) not null comment "code grant 방식에서 authorization request 때 등록했던 redirect_uri",
	code_challenge varchar(256) comment "pkce 인증에서 사용되는 challenge",
	code_challenge_method varchar(256) comment "pkce 인증에서 사용되는 challenge method",
	expires_at timestamp comment "만료시간, 등록후 10분 후으로 추가"
);

create table if not exists permissions(
	user_id varchar(128) not null,
	client_id varchar(128) not null,
	scopes varchar(128) comment "로그인한 유저의 권한(범위)",
	constraint fk_permission_user foreign key (user_id) references users (user_id),
	constraint fk_permission_client foreign key (client_id) references clients (client_id)
);