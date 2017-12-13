create sequence hibernate_sequence start with 1 increment by 1
create table blog (id bigint not null, active bit not null, blogurl varchar(255), description varchar(255), feedurl varchar(255), last_update_date datetime2, name varchar(255), published_date datetime2, primary key (id))
create table item (id bigint not null, author varchar(255), date datetime2, description varchar(MAX), link varchar(255), title varchar(255), blog_id bigint, primary key (id))
alter table blog add constraint UK_3glsfnj94ocln0rpsm9mvt9w6 unique (blogurl)
alter table item add constraint UK22udmt046cqgk74wryyrt50qu unique (link, blog_id)
alter table item add constraint FK60ndn1v2u4j38nfc5yahfkb7e foreign key (blog_id) references blog
