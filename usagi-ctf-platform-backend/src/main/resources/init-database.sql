create table if not exists usagi_users
(
    id           int unsigned auto_increment comment '用户id',
    username     varchar(20)             not null comment '用户名',
    password     char(32)                not null comment '密码',
    user_intro   varchar(100)            null comment '用户介绍',
    email        varchar(32)             null comment '邮箱',
    phone_number char(11)                null comment '电话号码',
    name         varchar(32)             not null comment '实名名字',
    id_card      char(18)                not null comment '身份证号',
    teams        varchar(100) default '' not null comment '加入的团队',
    is_captain   int          default 0  not null comment '是否为队长',
    constraint usagi_users_pk
        primary key (id),
    constraint usagi_users_uk
        unique (username)
)
    comment '用户相关';

create table if not exists usagi_teams
(
    id         int unsigned auto_increment comment '团队id',
    team_name  varchar(20)             not null comment '团队名称',
    team_intro varchar(100)            null comment '团队介绍',
    members    varchar(100)            not null comment '团队成员',
    games      varchar(100) default '' not null comment '参加的比赛',
    constraint usagi_teams_pk
        primary key (id),
    constraint usagi_teams_uk
        unique (team_name)
)
    comment '团队相关';

create table if not exists usagi_games
(
    id         int unsigned auto_increment comment '比赛id',
    game_name  varchar(20)        not null comment '比赛名称',
    game_intro text               null comment '比赛介绍',
    time_start datetime           not null comment '开始时间',
    time_end   datetime           not null comment '结束时间',
    is_open    bool default false not null comment '是否开放',
    constraint usagi_games_pk
        primary key (id)
)
    comment '比赛相关';
