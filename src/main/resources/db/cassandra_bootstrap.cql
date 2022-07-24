CREATE KEYSPACE IF NOT EXISTS errorreporting
  WITH REPLICATION = {
   'class' : 'SimpleStrategy',
   'replication_factor' : 1
  };