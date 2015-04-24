INSERT INTO `etr_tb_partyagreement` (`PAG_ID`, `CRE_DT`, `CRE_ID`, `MOD_DT`, `MOD_ID`, `PAG_AUTH_PTY_ID`, `PAG_DEL_PTY_ID`) VALUES (2,'2013-02-25 00:00:00','ORAZISA','2013-02-25 00:00:00','ORAZISA',81,79);
INSERT INTO `etr_tb_partyagreement` (`PAG_ID`, `CRE_DT`, `CRE_ID`, `MOD_DT`, `MOD_ID`, `PAG_AUTH_PTY_ID`, `PAG_DEL_PTY_ID`) VALUES (3,'2013-02-25 00:00:00','ORAZISA','2013-02-25 00:00:00','ORAZISA',79,82);

insert into etr_tb_endpoint(EDP_ID,EDP_MSG_TYPE,EDP_ACTIVE_FLAG,EDP_AUTH_FLAG,EDP_PROXY_HOST,EDP_PROXY_PORT,EDP_USE_PROXY,EDP_CRED_ID,EDP_ICA_ID,EDP_PTY_ID,EDP_PRO_ID,EDP_PROXY_CRED_ID,EDP_TRA_ID,EDP_BD_ID)VALUES(43,'TYPED',1,0,null,null,0,null,null,79,null,null,74,null);
insert into etr_tb_endpoint_jms (ID,EDP_JMS_CFACT_NM,EDP_JMS_DEST_JNDI_NM,EDP_JMS_INIT_CONT_FACT,EDP_JMS_MESS_CON_CLASS,EDP_JMS_PROV_URL,EDP_REPLY_TO_FLAG) values (43,'java:/ConnectionFactory','queue/ETRUSTEX_TO_EDELIVERY_AS4Queue',null,'eu.europa.ec.cipa.etrustex.integration.util.SoapOverJmsMessageConverter',null,0);

insert into etr_tb_metadata (MD_ID,MD_TYPE,MD_VALUE,PID_DOC_ID,PID_ICA_ID,PID_PRO_ID,PID_TRA_ID,MD_SENDER_ID) values (671,'SENDER_GATEWAY_NAME','receiverCN',null,null,null, null,null);

update etr_tb_certificate set CERT_HOLDER='CN=senderCN, OU = European Commission, O = DIGIT B1, S = Bruxelles, C = BE' where CERT_ID=40;