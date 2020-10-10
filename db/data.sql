insert into CONFIG_PROPERTY_TYPE (id,name,path_reference,priority) values (1,'Soap Action','RequestHeader/SOAPAction',1);
insert into CONFIG_PROPERTY_TYPE (id,name,path_reference,priority) values (2,'User Id','SoapHeader/UserId',2);

insert into mock_type (id,name) values (1,'LIVE');
insert into mock_type (id,name) values (2,'MOCK');
insert into mock_type (id,name) values (3,'SOAP_ERROR');
insert into mock_type (id,name) values (4,'HTTP_ERROR');