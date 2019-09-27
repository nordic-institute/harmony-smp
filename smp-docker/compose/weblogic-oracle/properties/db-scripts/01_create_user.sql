create tablespace smp_tblspace datafile 'smp_tblspace.dat'  size 10M autoextend on;
create temporary tablespace smp_tblspace_temp tempfile 'smp_tblspace_temp.dat' size 5M autoextend on;

create user smp identified by test default tablespace smp_tblspace temporary tablespace smp_tblspace_temp;

grant create session to smp;
grant create sequence to smp;
grant create table to smp;
grant unlimited tablespace to smp;
exit;
