drop table config_global if exists
	
create table config_global (
   id bigint not null,
	end_point_url varchar(255),
	no_of_rows_display integer,
	updated_date timestamp,
	mock_type_id bigint,
	primary key (id)
)

drop table config_property_level if exists

create table config_property_level (
   id bigint not null,
	updated_date timestamp,
	value varchar(255),
	config_property_type_id bigint,
	mock_type_id bigint,
	primary key (id)
)

drop table config_property_type if exists

create table config_property_type (
       id bigint not null,
        inserted_date timestamp,
        name varchar(255),
        path_reference varchar(255),
        priority integer,
        primary key (id)
    )


drop table mock_request_data if exists

create table mock_request_data (
       mock_id varchar(255) not null,
        archived_date timestamp,
        inserted_date timestamp,
        request_checksum varchar(255),
        request_data clob,
        primary key (mock_id)
    )
    
drop table mock_response_data if exists

create table mock_response_data (
   mock_id varchar(255) not null,
	archived_date timestamp,
	exception_object blob,
	inserted_date timestamp,
	response_data clob,
	soap_action varchar(255),
	status_code integer,
	primary key (mock_id)
)

drop table mock_type if exists
 
create table mock_type (
   id bigint not null,
	inserted_date timestamp,
	name varchar(255),
	primary key (id)
)
     
drop table response_headers if exists

create table response_headers (
   mock_id varchar(255) not null,
	name varchar(255),
	value varchar(255)
)

drop sequence if exists hibernate_sequence

create sequence hibernate_sequence start with 1 increment by 1

alter table config_global 
       add constraint FKsx3iohwomeuouovppo46fwcb8 
       foreign key (mock_type_id) 
       references mock_type
    
alter table config_property_level 
       add constraint FK47bgqvunohws1ornri5vpi6ti 
       foreign key (config_property_type_id) 
       references config_property_type
    
alter table config_property_level 
       add constraint FK3pdv63vej9pvykfx3c861a0yf 
       foreign key (mock_type_id) 
       references mock_type
    
alter table response_headers 
       add constraint FK6sxhj0ah0438h6685hhp3yud0 
       foreign key (mock_id) 
       references mock_response_data
	   
	   
	   
insert into CONFIG_PROPERTY_TYPE (id,name,path_reference,priority) values (1,'Soap Action','RequestHeader/SOAPAction',1);

insert into mock_type (id,name) values (1,'LIVE');
insert into mock_type (id,name) values (2,'MOCK');
insert into mock_type (id,name) values (3,'SOAP_ERROR');
insert into mock_type (id,name) values (4,'HTTP_ERROR');