create or replace PROCEDURE GET_NEXT_CLIENT_POSTID
(
  DT IN DATE
, PostId OUT nvarchar2
)
AS

NEXTNUM int;
BEGIN
  SELECT EXTRACT(YEAR FROM dt) INTO nextnum FROM dual;

if NEXTNUM = 2017 then
      select POSTID2017.nextVal into nextNum from dual;
   ELSIF  NEXTNUM = 2018 then
      select POSTID2018.nextVal into nextNum from dual;
   ELSIF  NEXTNUM = 2019 then
      select POSTID2019.nextVal into nextNum from dual;
   ELSIF  NEXTNUM = 2020 then
      select POSTID2020.nextVal into nextNum from dual;
   ELSIF  NEXTNUM = 2021 then
      select POSTID2021.nextVal into nextNum from dual;
   ELSIF  NEXTNUM = 2022 then
      select POSTID2022.nextVal into nextNum from dual;
   ELSIF  NEXTNUM = 2023 then
      select POSTID2023.nextVal into nextNum from dual;
   ELSIF  NEXTNUM = 2024 then
      select POSTID2024.nextVal into nextNum from dual;
   ELSIF  NEXTNUM = 2025 then
      select POSTID2025.nextVal into nextNum from dual;
   else
      select POSTID2026.nextVal into nextNum from dual;
end if;
PostId :=  LPAD(nextNum, 7, '0');
END GET_NEXT_CLIENT_POSTID;
