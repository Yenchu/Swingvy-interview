create table company (
    company_id bigint not null,
    name varchar(100) not null,
    primary key (company_id)
);

create table user (
    company_id bigint not null,
    user_id bigint not null,
    name varchar(100) not null,
    login_name varchar(100) not null,
    password varchar(100) not null,
    role varchar(10) not null,
    primary key (user_id),
    foreign key (company_id) references company(company_id)
);

create table attendance (
    attendance_id bigint not null AUTO_INCREMENT,
    company_id bigint not null,
    user_id bigint not null,
    clock_in_time timestamp not null,
    clock_out_time timestamp null,
    active_time int(11) null,
    break_time int(11) null,
    primary key (attendance_id)
);
create index idx_clock_in_time on attendance(clock_in_time);

create table break (
    break_id bigint not null AUTO_INCREMENT,
    attendance_id bigint not null,
    company_id bigint not null,
    user_id bigint not null,
    start_time timestamp not null,
    end_time timestamp null,
    primary key (break_id),
    foreign key (attendance_id) references attendance(attendance_id)
);