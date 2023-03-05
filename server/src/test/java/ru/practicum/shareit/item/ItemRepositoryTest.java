package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.shareit.UtilsForTest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase
public class ItemRepositoryTest {
    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldReturnAllItemsByText() {
        EntityManager entityManager = testEntityManager.getEntityManager();
        TypedQuery<Item> query = entityManager.createQuery("SELECT i " +
                "FROM Item i " +
                "WHERE (LOWER(i.name) LIKE LOWER(CONCAT('%', ?1, '%')) " +
                "OR LOWER(i.description) LIKE LOWER(CONCAT('%', ?1, '%')) ) " +
                "AND i.available IS TRUE", Item.class);

        User user = UtilsForTest.makeUser(1);
        userRepository.save(user);
        Item item1 = Item.builder()
                .id(1)
                .name("item")
                .description("about")
                .available(true)
                .owner(user)
                .request(null)
                .build();
        Item item2 = Item.builder()
                .id(2)
                .name("test")
                .description("about item")
                .available(true)
                .owner(user)
                .request(null)
                .build();
        Item item3 = Item.builder()
                .id(3)
                .name("unknown")
                .description("about")
                .available(true)
                .owner(user)
                .request(null)
                .build();
        assertThat(query.setParameter(1, "item").getResultList()).isEmpty();
        itemRepository.save(item1);
        itemRepository.save(item2);
        itemRepository.save(item3);

        assertThat(itemRepository.findAllByText("item", Pageable.unpaged()))
                .hasSize(2);
    }
}
