insert into Role(role) values('ROLE_CONSUMER');
insert into Role(role) values('ROLE_SELLER');

insert into UserDetails(username, password) values('jack','pass_word');
insert into UserDetails(username, password) values('bob','pass_word');
insert into UserDetails(username, password) values('apple','pass_word');
insert into UserDetails(username, password) values('glaxo','pass_word');

insert into Category(categoryName) values('Fashion');
insert into Category(categoryName) values('Electronics');
insert into Category(categoryName) values('Books');
insert into Category(categoryName) values('Groceries');
insert into Category(categoryName) values('Medicines');

insert into Cart(totalAmount, userId) values(20, 1);
insert into Cart(totalAmount, userId) values(0, 2);

insert into User_role(userId, roleId) values(1,1);
insert into User_role(userId, roleId) values(2,1);
insert into User_role(userId, roleId) values(3,2);
insert into User_role(userId, roleId) values(4,2);

insert into Product(price, productName, categoryId, sellerId) values( 29190, 'Apple iPad 10.2 8th Gen WiFi iOS Tablet', 2, 3);
insert into Product(price, productName, categoryId, sellerId) values( 10, 'Crocin pain relief tablet', 5, 4);

insert into CartProduct(cartId, productId, quantity) values(1, 2, 2);