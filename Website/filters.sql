USE pcbuilder;

CREATE TABLE onderdeel(
	type VARCHAR(45),
	PRIMARY KEY (type)
);

CREATE TABLE merk (
	mid INT AUTO_INCREMENT,
	naam VARCHAR(25),
	PRIMARY KEY(mid, naam)
);

CREATE TABLE tussentabel(
	onderdeeltype VARCHAR(45),
	merkmid INT,
	FOREIGN KEY (onderdeeltype) REFERENCES onderdeel(type),
	FOREIGN KEY (merkmid) REFERENCES merk(mid)
);

CREATE TABLE socket (
	type VARCHAR(45),
	merkmid INT,
	PRIMARY KEY (type),
	FOREIGN KEY (merkmid) REFERENCES merk(mid)
);

CREATE TABLE aansluiting (
	type VARCHAR(45),
	onderdeeltype VARCHAR(45),
	PRIMARY KEY (type),
	FOREIGN KEY (onderdeeltype) REFERENCES onderdeel(type)
);

CREATE TABLE hardeschijf (
	type VARCHAR(45),
	aansluitingtype VARCHAR(45),
	PRIMARY KEY (type, aansluitingtype),
	FOREIGN KEY (aansluitingtype) REFERENCES aansluiting(type)
);

CREATE TABLE geheugen (
	type VARCHAR(45),
	PRIMARY KEY (type)
);

CREATE TABLE formfactor (
	formfactor VARCHAR(45),
	PRIMARY KEY (formfactor)
);

CREATE TABLE grafischekaart (
	aansluitingtype VARCHAR(45),
	FOREIGN KEY (aansluitingtype) REFERENCES aansluiting(type)
);

INSERT INTO  `pcbuilder`.`onderdeel` (`type`)
VALUES ('Moederbord'), ('Processor'), ('Processor koeler'), ('Hardeschijf'), ('Geheugen'), ('Grafischekaart'), ('Voeding'), ('Behuizing'), ('Besturingssysteem');

INSERT INTO  `pcbuilder`.`merk` (`naam`)
VALUES ('AMD'), ('Intel'), ('Matrox'), ('Nvidia');

INSERT INTO  `pcbuilder`.`tussentabel` (`onderdeeltype`, `merkmid`)
VALUES ('Moederbord', '1'), ('Moederbord', '2'), ('Processor', '1'), ('Processor', '2'), ('Processor koeler', '1'), ('Processor koeler', '2'),('Grafischekaart', '1'),('Grafischekaart', '3'),('Grafischekaart', '4');

INSERT INTO  `pcbuilder`.`socket` (`type`, `merkmid`)
VALUES ('Socket 940', 1), ('Socket AM1', 1), ('Socket AM3',1), ('Socket AM3+',1), ('Socket C32',1), ('Socket F', 1), ('Socket FM1',1),('Socket FM2',1),('Socket FM2+',1),('Socket G34',1), ('Socket 478',2), ('Socket 771',2), ('Socket 775',2), ('Socket 946',2), ('Socket 988',2), ('Socket 1150',2), ('Socket 1155',2), ('Socket 1366',2), ('Socket 2011',2), ('Socket 2011-3',2);

INSERT INTO  `pcbuilder`.`aansluiting` (`type`, `onderdeeltype`)
VALUES ('AGP 8x', 'Grafischekaart'), ('PCI-e 2.0 x16', 'Grafischekaart'), ('PCI-e 3.0 x16', 'Grafischekaart'), ('PCI-e x1', 'Grafischekaart'), ('PCI-e x8', 'Grafischekaart'), ('PCI-e x16', 'Grafischekaart'), ('AGP 4x', 'Grafischekaart'), ('PCI-e 2.0 1x', 'Grafischekaart'), ('PCI 2.2', 'Grafischekaart'), ('PCI 3.0', 'Grafischekaart'), ('mini-PCI Express', 'Hardeschijf'), ('PCI-e 2.0 x1', 'Hardeschijf'), ('PCI-e 2.0 x4', 'Hardeschijf'), ('PCI-e 2.0 x8', 'Hardeschijf'), ('PCI-e 3.0 x4', 'Hardeschijf'), ('PCI-e 3.0 x8', 'Hardeschijf'), ('PCI-e x4', 'Hardeschijf'), ('M.2', 'Hardeschijf'), ('mSATA', 'Hardeschijf'), ('SATA-150', 'Hardeschijf'), ('SATA-300', 'Hardeschijf'), ('SATA-600', 'Hardeschijf'), ('SATA Express', 'Hardeschijf'), ('PCI Express', 'Hardeschijf'), ('PATA-100', 'Hardeschijf'), ('PATA-133', 'Hardeschijf'), ('PATA-33', 'Hardeschijf');

INSERT INTO  `pcbuilder`.`hardeschijf` (`type`, `aansluitingtype`)
VALUES ('Solid State Drive', 'mini-PCI Express'), ('Solid State Drive', 'PCI-e 2.0 x1'), ('Solid State Drive', 'PCI-e 2.0 x4'), ('Solid State Drive', 'PCI-e 2.0 x8'), ('Solid State Drive', 'PCI-e 3.0 x4'), ('Solid State Drive', 'PCI-e 3.0 x8'), ('Solid State Drive', 'PCI-e x4'), ('Solid State Drive', 'M.2'), ('Solid State Drive', 'mSATA'), ('Solid State Drive', 'SATA-150'), ('Solid State Drive', 'SATA-300'), ('Solid State Drive', 'SATA-600'), ('Solid State Drive', 'SATA Express'), ('Solid State Drive', 'Expresscard'), ('Solid State Drive', 'PCI Express'), ('Interne hardeschijf', 'mSATA'), ('Interne hardeschijf', 'SATA-150'), ('Interne hardeschijf', 'SATA-300'), ('Interne hardeschijf', 'SATA-600'), ('Interne hardeschijf', 'PATA-100'), ('Interne hardeschijf', 'PATA-133'), ('Interne hardeschijf', 'PATA-33');

INSERT INTO  `pcbuilder`.`geheugen` (`type`)
VALUES ('DDR'), ('DDR (SODIMM)'), ('DDR2'), ('DDR2 (SODIMM)'), ('DDR3'), ('DDR3 (SODIMM)'), ('DDR4'), ('FB-DIMM'), ('mDIMM'), ('Rambus DRAM'), ('SDR'), ('SDR (SODIMM)');

INSERT INTO  `pcbuilder`.`formfactor` (`formfactor`)
VALUES ('ATX (Standard)'), ('EATX (Extended ATX)'), ('Micro-ATX (µATX)'), ('Mini-DTX'), ('Mini-ITX'), ('XL-ALX'), ('BTX'), ('CEB'), ('DTX'), ('EEB'), ('Enchanced EATD (EEATX)'), ('Flex-ATX'), ('HPTX'), ('Micro-BTX'), ('SFF'), ('SSI'), ('Thin Mini-ITX'), ('UCFF');

INSERT INTO  `pcbuilder`.`grafischekaart` (`aansluitingtype`)
VALUES ('AGP 8x'), ('PCI-e 2.0 x16'), ('PCI-e 3.0 x16'), ('PCI-e x1'), ('PCI-e x8'), ('PCI-e x16'), ('AGP 4x'), ('PCI-e 2.0 1x'), ('PCI 2.2'), ('PCI 3.0');