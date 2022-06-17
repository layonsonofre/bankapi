create table conta (
  conta_id bigint not null,
  agencia varchar(255) not null,
  conta varchar(255) not null,
  saldo decimal(65, 2) not null default 0,
  cliente_id bigint not null,
  constraint pk_conta primary key (conta_id)
);

alter table conta add constraint fk_cliente foreign key (cliente_id) references cliente(cliente_id);