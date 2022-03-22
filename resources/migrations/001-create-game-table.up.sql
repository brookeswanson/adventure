create table game (
  id bigserial not null unique primary key,
  player_id text not null unique,
  current_state jsonb not null,
  created_at timestamp default current_timestamp,
  updated_at timestamp default current_timestamp
);
