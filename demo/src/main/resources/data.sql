-- Insert sample promoters
INSERT INTO promoters (name, email, organization) VALUES ('Cruz Vermelha Portuguesa', 'contato@cruzvermelha.pt', 'Cruz Vermelha');
INSERT INTO promoters (name, email, organization) VALUES ('Banco Alimentar', 'info@bancoalimentar.pt', 'Banco Alimentar Contra a Fome');
INSERT INTO promoters (name, email, organization) VALUES ('AMI - Assistência Médica Internacional', 'geral@ami.org.pt', 'AMI');
INSERT INTO promoters (name, email, organization) VALUES ('Habitat for Humanity Portugal', 'info@habitat.pt', 'Habitat for Humanity');
INSERT INTO promoters (name, email, organization) VALUES ('Associação Salvador', 'contato@associacaosalvador.pt', 'Associação Salvador');

-- Insert sample benefits (UA)
INSERT INTO benefits (name, description, points_required, category, provider, image_url, active, created_at) VALUES ('Desconto Cantina UA', '10% de desconto na cantina da Universidade de Aveiro', 100, 'UA', 'Universidade de Aveiro', NULL, true, CURRENT_TIMESTAMP);
INSERT INTO benefits (name, description, points_required, category, provider, image_url, active, created_at) VALUES ('Acesso Biblioteca Premium', 'Acesso a sala premium da biblioteca por 1 mes', 200, 'UA', 'Universidade de Aveiro', NULL, true, CURRENT_TIMESTAMP);

-- Insert sample benefits (PARTNER)
INSERT INTO benefits (name, description, points_required, category, provider, image_url, active, created_at) VALUES ('Desconto Cinema NOS', '20% de desconto em bilhetes de cinema', 150, 'PARTNER', 'Cinema NOS', NULL, true, CURRENT_TIMESTAMP);
INSERT INTO benefits (name, description, points_required, category, provider, image_url, active, created_at) VALUES ('Voucher Restaurante', 'Voucher de 10 euros no Restaurante Sabor', 300, 'PARTNER', 'Restaurante Sabor', NULL, true, CURRENT_TIMESTAMP);
INSERT INTO benefits (name, description, points_required, category, provider, image_url, active, created_at) VALUES ('Desconto Estacionamento', '50% de desconto no estacionamento mensal', 250, 'PARTNER', 'ParkAveiro', NULL, true, CURRENT_TIMESTAMP);
