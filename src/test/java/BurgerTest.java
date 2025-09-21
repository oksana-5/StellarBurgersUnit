import org.assertj.core.api.SoftAssertions;
import org.hamcrest.MatcherAssert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import praktikum.Bun;
import praktikum.Burger;
import praktikum.Ingredient;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static praktikum.IngredientType.FILLING;

@RunWith(MockitoJUnitRunner.class)

public class BurgerTest {

    private Burger burger;

    @Mock
    Bun mockBun;

    @Mock
    Ingredient mockIngredient1;

    @Mock
    Ingredient mockIngredient2;

    @Mock
    Ingredient mockIngredient3;

    @Before
    public void setUp() {
        burger = new Burger();
    }

    @Test
    public void setBunsTest() {
        burger.setBuns(mockBun);
        assertEquals("Значение поля bun должно соответствовать переданному объекту Bun", mockBun, burger.bun);
    }

    @Test
    public void addIngredientTest() {
        burger.addIngredient(mockIngredient1);

        SoftAssertions softly = new SoftAssertions();

        softly.assertThat(burger.ingredients.size())
                .as("Ингредиент должен добавиться в список ингредиентов")
                .isEqualTo(1);

        softly.assertThat(burger.ingredients.get(0))
                .as("Добавленный ингредиент должен совпадать с mock-ингредиентом")
                .isEqualTo(mockIngredient1);
        softly.assertAll();
    }

    @Test
    public void removeIngredientTest() {
        burger.ingredients.add(mockIngredient1);
        burger.ingredients.add(mockIngredient2);
        burger.ingredients.add(mockIngredient3);
        burger.removeIngredient(2);

        SoftAssertions softly = new SoftAssertions();

        softly.assertThat(burger.ingredients.size())
                .as("Должно остаться 2 ингредиента")
                .isEqualTo(2);

        softly.assertThat(burger.ingredients.get(0))
                .as("Первый ингредиент должен остаться")
                .isEqualTo(mockIngredient1);

        softly.assertThat(burger.ingredients.get(1))
                .as("Последний ингредиент должен сдвинуться")
                .isEqualTo(mockIngredient2);

        softly.assertThat(burger.ingredients.contains(mockIngredient3))
                .as("Второй ингредиент должен быть удален")
                .isFalse();

    }

    @Test
    public void moveIngredientTest() {
        burger.ingredients.add(mockIngredient1);
        burger.ingredients.add(mockIngredient2);
        burger.ingredients.add(mockIngredient3);
        burger.moveIngredient(0, 2);

        SoftAssertions softly = new SoftAssertions();

        softly.assertThat(burger.ingredients.size())
                .as("Количество ингредиентов не должно измениться")
                .isEqualTo(3);

        softly.assertThat(burger.ingredients.get(0))
                .as("Первый элемент должен стать ingredient1")
                .isEqualTo(mockIngredient2);

        softly.assertThat(burger.ingredients.get(1))
                .as("Второй элемент должен стать ingredient2")
                .isEqualTo(mockIngredient3);

        softly.assertThat(burger.ingredients.get(2))
                .as("Третий элемент должен стать ingredient0")
                .isEqualTo(mockIngredient1);

    }

    @RunWith(Parameterized.class)
    public static class BurgerPriceTest {

        private final float bunPrice;
        private final float ingredientPrice1;
        private final float ingredientPrice2;
        private final float expectedPrice;

        private Burger burger;
        private Bun mockBun;
        private Ingredient mockIngredient1;
        private Ingredient mockIngredient2;

        public BurgerPriceTest(float bunPrice, float ingredientPrice1, float ingredientPrice2, float expectedPrice) {
            this.bunPrice = bunPrice;
            this.ingredientPrice1 = ingredientPrice1;
            this.ingredientPrice2 = ingredientPrice2;
            this.expectedPrice = expectedPrice;
        }

        @Parameterized.Parameters
        public static Object[][] getTestData() {
            return new Object[][]{
                    {100.0f, 50.0f, 30.0f, 280.0f},
                    {50.0f, 30.0f, 0.0f, 130.0f},
                    {0.0f, 0.0f, 0.0f, 0.0f},
                    {99.99f, 49.99f, 29.99f, 279.96f},
            };
        }

        @Test
        public void getPriceTest() {
            burger = new Burger();
            mockBun = mock(Bun.class);
            mockIngredient1 = mock(Ingredient.class);
            mockIngredient2 = mock(Ingredient.class);

            when(mockBun.getPrice()).thenReturn(bunPrice);
            when(mockIngredient1.getPrice()).thenReturn(ingredientPrice1);
            when(mockIngredient2.getPrice()).thenReturn(ingredientPrice2);

            burger.bun = mockBun;
            burger.ingredients.add(mockIngredient1);
            burger.ingredients.add(mockIngredient2);

            float actualPrice = burger.getPrice();

            assertEquals("Цена должна быть рассчитана правильно", expectedPrice, actualPrice, 0.01f);

            verify(mockBun, times(1)).getPrice();
            verify(mockIngredient1, times(1)).getPrice();
            verify(mockIngredient2, times(1)).getPrice();
        }
    }

    @Test
    public void getReceiptTest() {

        burger.bun = mockBun;
        burger.ingredients.add(mockIngredient2);

        Burger burgerSpy = Mockito.spy(burger);

        when(mockBun.getName()).thenReturn("white bun");
        when(mockIngredient2.getType()).thenReturn(FILLING);
        when(mockIngredient2.getName()).thenReturn("dinosaur");
        when(burgerSpy.getPrice()).thenReturn(500.0f);

        String actualReceipt = burgerSpy.getReceipt();

        String expectedReceipt;
        if (actualReceipt.contains("\r\n\r\nPrice:")) {
            expectedReceipt = String.format("(==== %s ====)%n", mockBun.getName()) +
                    String.format("= %s %s =%n", mockIngredient2.getType().name().toLowerCase(), mockIngredient2.getName()) +
                    String.format("(==== %s ====)%n", mockBun.getName()) +
                    String.format("%nPrice: %.6f%n", (mockBun.getPrice() * 2) + mockIngredient2.getPrice());
        } else {
            expectedReceipt = String.format("(==== %s ====)%n", mockBun.getName()) +
                    String.format("= %s %s =%n", mockIngredient2.getType().name().toLowerCase(), mockIngredient2.getName()) +
                    String.format("(==== %s ====)%n", mockBun.getName()) +
                    String.format("Price: %.6f%n", (mockBun.getPrice() * 2) + mockIngredient2.getPrice());
        }

        MatcherAssert.assertThat("Неверный рецепт",
                burger.getReceipt(),
                equalTo(expectedReceipt));
    }

}