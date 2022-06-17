create table movimentacao (
  movimentacao_id bigint not null,
  valor decimal(65, 2) not null,
  operacao int not null,
  data timestamp not null default current_timestamp,
  conta_id bigint not null,
  constraint pk_movimentacao primary key (movimentacao_id)
);

alter table movimentacao add constraint fk_conta foreign key (conta_id) references conta(conta_id);