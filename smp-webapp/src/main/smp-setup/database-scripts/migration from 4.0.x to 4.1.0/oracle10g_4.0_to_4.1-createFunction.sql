--- create function for converting clobs to blobs
--  function is used during migration of data from 4.0 to  4.1
CREATE OR REPLACE FUNCTION clob_to_blob(p_clob IN CLOB) RETURN BLOB IS
  v_blob BLOB;
  v_offset NUMBER DEFAULT 1;
  v_amount NUMBER DEFAULT 4096;
  v_offsetwrite NUMBER DEFAULT 1;
  v_amountwrite NUMBER;
  v_buffer VARCHAR2(4096 CHAR);
BEGIN
  dbms_lob.createtemporary(v_blob, TRUE);

  Begin
    LOOP
      dbms_lob.READ (lob_loc => p_clob,
                     amount  => v_amount,
                     offset  => v_offset,
                     buffer  => v_buffer);

      v_amountwrite := utl_raw.length (r => utl_raw.cast_to_raw(c => v_buffer));

      dbms_lob.WRITE (lob_loc => v_blob,
                      amount  => v_amountwrite,
                      offset  => v_offsetwrite,
                      buffer  => utl_raw.cast_to_raw(v_buffer));

      v_offsetwrite := v_offsetwrite + v_amountwrite;

      v_offset := v_offset + v_amount;
      v_amount := 4096;
    END LOOP;
  EXCEPTION
    WHEN no_data_found THEN
      NULL;
  End;
  RETURN v_blob;
END clob_to_blob;
