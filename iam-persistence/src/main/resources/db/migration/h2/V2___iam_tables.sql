--
-- Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2016-2018
--
-- Licensed under the Apache License, Version 2.0 (the "License");
-- you may not use this file except in compliance with the License.
-- You may obtain a copy of the License at
--
--     http://www.apache.org/licenses/LICENSE-2.0
--
-- Unless required by applicable law or agreed to in writing, software
-- distributed under the License is distributed on an "AS IS" BASIS,
-- WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
-- See the License for the specific language governing permissions and
-- limitations under the License.
--

CREATE TABLE iam_account (ID BIGINT IDENTITY NOT NULL, active BOOLEAN NOT NULL, CREATIONTIME TIMESTAMP NOT NULL, LASTUPDATETIME TIMESTAMP NOT NULL, PASSWORD VARCHAR(128), USERNAME VARCHAR(128) NOT NULL UNIQUE, UUID VARCHAR(36) NOT NULL UNIQUE, user_info_id BIGINT, PRIMARY KEY (ID));
CREATE TABLE iam_authority (ID BIGINT IDENTITY NOT NULL, auth VARCHAR(128) NOT NULL UNIQUE, PRIMARY KEY (ID));
CREATE TABLE iam_group (ID BIGINT IDENTITY NOT NULL, CREATIONTIME TIMESTAMP NOT NULL, DESCRIPTION VARCHAR(512), LASTUPDATETIME TIMESTAMP NOT NULL, NAME VARCHAR(255) NOT NULL UNIQUE, UUID VARCHAR(36) NOT NULL UNIQUE, PRIMARY KEY (ID));
CREATE TABLE iam_oidc_id (ID BIGINT IDENTITY NOT NULL, ISSUER VARCHAR(256) NOT NULL, SUBJECT VARCHAR(256) NOT NULL, account_id BIGINT, PRIMARY KEY (ID));
CREATE TABLE iam_saml_id (ID BIGINT IDENTITY NOT NULL, IDPID VARCHAR(256) NOT NULL, USERID VARCHAR(256) NOT NULL, account_id BIGINT, PRIMARY KEY (ID));
CREATE TABLE iam_ssh_key (ID BIGINT IDENTITY NOT NULL, fingerprint VARCHAR(48) NOT NULL UNIQUE, LABEL VARCHAR(36) NOT NULL UNIQUE, is_primary BOOLEAN, val LONGVARCHAR, ACCOUNT_ID BIGINT, PRIMARY KEY (ID));
CREATE TABLE iam_user_info (ID BIGINT IDENTITY NOT NULL, BIRTHDATE VARCHAR, EMAIL VARCHAR(128) NOT NULL, EMAILVERIFIED BOOLEAN, FAMILYNAME VARCHAR(64) NOT NULL, GENDER VARCHAR, GIVENNAME VARCHAR(64) NOT NULL, LOCALE VARCHAR, MIDDLENAME VARCHAR(64), NICKNAME VARCHAR, PHONENUMBER VARCHAR, PHONENUMBERVERIFIED BOOLEAN, PICTURE VARCHAR, PROFILE VARCHAR, WEBSITE VARCHAR, ZONEINFO VARCHAR, ADDRESS_ID BIGINT, DTYPE VARCHAR(31), PRIMARY KEY (ID));
CREATE TABLE iam_x509_cert (ID BIGINT IDENTITY NOT NULL, CERTIFICATESUBJECT VARCHAR(128) NOT NULL UNIQUE, LABEL VARCHAR(36) NOT NULL UNIQUE, is_primary BOOLEAN, ACCOUNT_ID BIGINT, PRIMARY KEY (ID));
CREATE TABLE iam_account_authority (account_id BIGINT NOT NULL, authority_id BIGINT NOT NULL, PRIMARY KEY (account_id, authority_id));
CREATE TABLE iam_account_group (account_id BIGINT NOT NULL, group_id BIGINT NOT NULL, PRIMARY KEY (account_id, group_id));
ALTER TABLE iam_account ADD CONSTRAINT FK_iam_account_user_info_id FOREIGN KEY (user_info_id) REFERENCES iam_user_info (ID);
ALTER TABLE iam_oidc_id ADD CONSTRAINT FK_iam_oidc_id_account_id FOREIGN KEY (account_id) REFERENCES iam_account (ID);
ALTER TABLE iam_saml_id ADD CONSTRAINT FK_iam_saml_id_account_id FOREIGN KEY (account_id) REFERENCES iam_account (ID);
ALTER TABLE iam_ssh_key ADD CONSTRAINT FK_iam_ssh_key_ACCOUNT_ID FOREIGN KEY (ACCOUNT_ID) REFERENCES iam_account (ID);
ALTER TABLE iam_x509_cert ADD CONSTRAINT FK_iam_x509_cert_ACCOUNT_ID FOREIGN KEY (ACCOUNT_ID) REFERENCES iam_account (ID);
ALTER TABLE iam_account_authority ADD CONSTRAINT FK_iam_account_authority_authority_id FOREIGN KEY (authority_id) REFERENCES iam_authority (ID);
ALTER TABLE iam_account_authority ADD CONSTRAINT FK_iam_account_authority_account_id FOREIGN KEY (account_id) REFERENCES iam_account (ID);
ALTER TABLE iam_account_group ADD CONSTRAINT FK_iam_account_group_account_id FOREIGN KEY (account_id) REFERENCES iam_account (ID);
ALTER TABLE iam_account_group ADD CONSTRAINT FK_iam_account_group_group_id FOREIGN KEY (group_id) REFERENCES iam_group (ID);