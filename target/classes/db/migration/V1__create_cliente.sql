create sequence hibernate_sequence increment by 1 start with 1;

create table cliente (
  cliente_id bigint not null,
  nome varchar(255) not null,
  constraint pk_cliente primary key (cliente_id)
);
