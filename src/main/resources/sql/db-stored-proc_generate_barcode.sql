CREATE or replace PROCEDURE                                                   Get_Next_BarCode
(
  PCP_UUID IN POSTCODEPOOL.UUID%type
, RESULT_BAR OUT VARCHAR2
, STATUS out int
) AS

PO_CODE INTEGER;


SEQNAME Varchar2(10);

BEGIN
  STATUS:=-1; RESULT_BAR:='';
  IF   STATUS = -1 then
    Begin
    -- вернули индекс отделения по PostCodePool_id
     SELECT POSTCODE into PO_CODE FROM POSTCODEPOOL where UUID = PCP_UUID ;
     EXCEPTION  WHEN NO_DATA_FOUND THEN
          STATUS :=  -3; -- POST_OFFICE NOT FOUND
    END;
  End IF;
  IF STATUS  = -1 then -- если индекс получен
   SeqName := ('PD'  || LPAD(PO_CODE, 5, '0')) ;  -- имя для счетчика
   --DBMS_OUTPUT.PUT_LINE('before exit check seqName='|| seqName);
    Begin
      SELECT 2 INTO STATUS FROM user_sequences where SEQUENCE_NAME =  SeqName; -- есть ли такая последовательность?
      exception
       WHEN NO_DATA_FOUND THEN
          STATUS := -2;
    end;
--    DBMS_OUTPUT.PUT_LINE('RES=' || res );
    IF STATUS = -2  then  -- если  последовательности нет - создадим ее
--      DBMS_OUTPUT.PUT_LINE('..........create sequence');
      execute immediate 'CREATE SEQUENCE ' || SeqName || ' INCREMENT BY 1 START WITH 1 MINVALUE 1 NOCACHE ORDER MAXVALUE 9999999';
    END IF;

--    DBMS_OUTPUT.PUT_LINE('after select DEPT_ID :' || DEPT_ID);
    execute immediate 'SELECT '||SeqName||'.nextval from dual' into STATUS; -- получим очередное значение счетчика
      -- INSERT INTO BARCODE_RESERVATION(DEPT_ID,CODE,CORR_CODE) Values (DEPT_ID, Substr('0000000'||STATUS,-7), CALC_CORR_CODE( 10000000*PO_CODE+STATUS ) ) RETURNING ID into STATUS ;
    RESULT_BAR := Substr('0000000'||STATUS,-7) || CALC_CORR_CODE( 10000000*PO_CODE+STATUS) ; --вернем счетчик + 1 символ контрольного кода

--  commit should be fired from calling code

  END IF;

END Get_Next_BarCode;
