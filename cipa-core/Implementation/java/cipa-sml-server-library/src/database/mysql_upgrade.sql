RENAME TABLE `sml_migrate` TO `migrate`;
ALTER TABLE `migrate` change `recipient_participant_identifier_scheme` `scheme` varchar(200) NOT NULL;
ALTER TABLE `migrate` change `recipient_participant_identifier_value` `rec_value` varchar(200) NOT NULL;


RENAME TABLE `sml_allowed_wildcard_schemes` TO `allowed_wildcard_schemes`;
RENAME TABLE `sml_service_metadata_publisher` TO `service_metadata_publisher`;


ALTER TABLE `sml_user` change `username` `sml_username` varchar(200) NOT NULL;
ALTER TABLE `sml_user` change `password` `sml_password` text;


RENAME TABLE `sml_recipient_participant_identifier` TO `recipient_part_identifier`;
ALTER TABLE `recipient_part_identifier` change `recipient_participant_identifier_scheme` `scheme` varchar(200) NOT NULL;
ALTER TABLE `recipient_part_identifier` change `recipient_participant_identifier_value` `rec_value` varchar(200) NOT NULL;