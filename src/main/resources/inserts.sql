INSERT INTO public.data_pool (enum_name, enum_label) VALUES( 'UserStatus', 'unconfirmedUser');
INSERT INTO public.data_pool (enum_name, enum_label) VALUES( 'UserStatus', 'VerifiedUser');
INSERT INTO public.data_pool (enum_name, enum_label) VALUES( 'UserStatus', 'BannedUser');
INSERT INTO public.data_pool (enum_name, enum_label) VALUES( 'UserStatus', 'BanAppealingUser');

INSERT INTO public.data_pool (enum_name, enum_label) VALUES( 'LenderStatus', 'unconfirmedLender');
INSERT INTO public.data_pool (enum_name, enum_label) VALUES( 'LenderStatus', 'VerifiedLender');
INSERT INTO public.data_pool (enum_name, enum_label) VALUES( 'LenderStatus', 'BannedLender');
INSERT INTO public.data_pool (enum_name, enum_label) VALUES( 'LenderStatus', 'BanAppealingLender');

INSERT INTO public.data_pool (enum_name, enum_label) VALUES( 'RenterStatus', 'unconfirmedRenter');
INSERT INTO public.data_pool (enum_name, enum_label) VALUES( 'RenterStatus', 'VerifiedRenter');
INSERT INTO public.data_pool (enum_name, enum_label) VALUES( 'RenterStatus', 'BannedRenter');
INSERT INTO public.data_pool (enum_name, enum_label) VALUES( 'RenterStatus', 'BanAppealingRenter');

INSERT INTO public.data_pool (enum_name, enum_label) VALUES( 'VehicleStatus', 'unconfirmedVehicle');
INSERT INTO public.data_pool (enum_name, enum_label) VALUES( 'VehicleStatus', 'VerifiedVehicle');
INSERT INTO public.data_pool (enum_name, enum_label) VALUES( 'VehicleStatus', 'BannedVehicle');
INSERT INTO public.data_pool (enum_name, enum_label) VALUES( 'VehicleStatus', 'BanAppealingVehicle');
INSERT INTO public.data_pool (enum_name, enum_label) VALUES( 'VehicleStatus', 'UserRemovedVehicle');

INSERT INTO public.data_pool (enum_name, enum_label) VALUES( 'ContractualStatus', 'Accepted');
INSERT INTO public.data_pool (enum_name, enum_label) VALUES( 'ContractualStatus', 'Rejected');
INSERT INTO public.data_pool (enum_name, enum_label) VALUES( 'ContractualStatus', 'Active');
INSERT INTO public.data_pool (enum_name, enum_label) VALUES( 'ContractualStatus', 'Finished');
INSERT INTO public.data_pool (enum_name, enum_label) VALUES( 'ContractualStatus', 'Waiting');
INSERT INTO public.data_pool (enum_name, enum_label) VALUES( 'ContractualStatus', 'Canceled');

INSERT INTO public.data_pool (enum_name, enum_label) VALUES( 'location', 'Korce');
INSERT INTO public.data_pool (enum_name, enum_label) VALUES( 'location', 'Tirane');
INSERT INTO public.data_pool (enum_name, enum_label) VALUES( 'location', 'Durres');
INSERT INTO public.data_pool (enum_name, enum_label) VALUES( 'location', 'Shkoder');
INSERT INTO public.data_pool (enum_name, enum_label) VALUES( 'location', 'Vlore');
INSERT INTO public.data_pool (enum_name, enum_label) VALUES( 'location', 'Fier');
INSERT INTO public.data_pool (enum_name, enum_label) VALUES( 'location', 'Lushnje');
INSERT INTO public.data_pool (enum_name, enum_label) VALUES( 'location', 'Berat');
INSERT INTO public.data_pool (enum_name, enum_label) VALUES( 'location', 'Sarande');
INSERT INTO public.data_pool (enum_name, enum_label) VALUES( 'location', 'Diber');
INSERT INTO public.data_pool (enum_name, enum_label) VALUES( 'location', 'Elbasan');
INSERT INTO public.data_pool (enum_name, enum_label) VALUES( 'location', 'Librazhd');

INSERT INTO public.data_pool (enum_name, enum_label) VALUES( 'transmissionType', 'Automatic');
INSERT INTO public.data_pool (enum_name, enum_label) VALUES( 'transmissionType', 'Manual');
INSERT INTO public.data_pool (enum_name, enum_label) VALUES( 'transmissionType', 'Semi-Automatic');

INSERT INTO public.data_pool (enum_name, enum_label) VALUES( 'engineType', 'Gasoline');
INSERT INTO public.data_pool (enum_name, enum_label) VALUES( 'engineType', 'Diesel');
INSERT INTO public.data_pool (enum_name, enum_label) VALUES( 'engineType', 'Electro/Hybrid');

INSERT INTO public.data_pool (enum_name, enum_label) VALUES( 'vehicleType', 'Cars');
INSERT INTO public.data_pool (enum_name, enum_label) VALUES( 'vehicleType', 'Vans&Trucks');
INSERT INTO public.data_pool (enum_name, enum_label) VALUES( 'vehicleType', 'Motorcycles');

INSERT INTO public.data_pool (enum_name, enum_label) VALUES( 'appealStatus', 'Decided');
INSERT INTO public.data_pool (enum_name, enum_label) VALUES( 'appealStatus', 'Pending-active');
INSERT INTO public.data_pool (enum_name, enum_label) VALUES( 'appealStatus', 'Pending-repetition');