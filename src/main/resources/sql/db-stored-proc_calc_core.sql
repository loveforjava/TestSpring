CREATE or replace FUNCTION                          CALC_CORR_CODE
(
  BAR_CODE IN INT
) RETURN INT AS
  CUR_MULT INTEGER := 2;
  BARC NVARCHAR2(13);
  lTmp int := 0;
  lPos int := 0;
  lMult int := 0;
  tBar int;
BEGIN
  lMult := 2;
  lTmp :=0;
  tBar := BAR_CODE;
--   DBMS_OUTPUT.PUT_LINE('BAR:'||tBar);
  WHILE tBar>0 LOOP
     lPos := tBar - TRUNC( tBar / 10.0,0)*10;
      --DBMS_OUTPUT.PUT_LINE('TRUNC:'||TRUNC( tBar / 10.0,0));
     tBar := (tBAR - lPos ) / 10;
--     DBMS_OUTPUT.PUT_LINE('='||lPos||'*'||lMult||'=: '||(lPos * lMult));
     lTmp := lTmp + ( lPos * lMult);
     lMult := lMult + 1;
     IF lMult > 7 then
        lMult := 2;
     end if;
  END LOOP;
--  BARC := RP('0000000000000'||BAR_CODE,13);
--  DBMS_OUTPUT.PUT_LINE('lTmp='||lTmp);
  lTmp := MOD (lTmp, 11);
  if lTmp <> 0 then
    lTmp :=11-lTmp;
  end if;
  if lTmp = 10 then
    lTmp := 0;
        elsif lTmp = 11 then
    lTmp := 0;
  End if;


--   DBMS_OUTPUT.PUT_LINE('Got into create sequence: '||lTMP);
  return lTMP;

END CALC_CORR_CODE;
