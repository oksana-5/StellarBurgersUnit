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
    Ingredient mockIngredientHotSauce;

    @Mock
    Ingredient mockIngredientDinosaurFilling;

    @Mock
    Ingredient mockIngredientChiliSauce;

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
        burger.addIngredient(mockIngredientHotSauce);

        SoftAssertions softly = new SoftAssertions();

        softly.assertThat(burger.ingredients.size())
                .as("Ингредиент должен добавиться в список ингредиентов")
                .isEqualTo(1);

        softly.assertThat(burger.ingredients.get(0))
                .as("Добавленный ингредиент должен совпадать с mock-ингредиентом")
                .isEqualTo(mockIngredientHotSauce);
        softly.assertAll();
    }

    @Test
    public void removeIngredientTest() {
        burger.ingredients.add(mockIngredientHotSauce);
        burger.ingredients.add(mockIngredientDinosaurFilling);
        burger.ingredients.add(mockIngredientChiliSauce);
        burger.removeIngredient(2);

        SoftAssertions softly = new SoftAssertions();

        softly.assertThat(burger.ingredients.size())
                .as("Должно остаться 2 ингредиента")
                .isEqualTo(2);

        softly.assertThat(burger.ingredients.get(0))
                .as("Первый ингредиент должен остаться")
                .isEqualTo(mockIngredientHotSauce);

        softly.assertThat(burger.ingredients.get(1))
                .as("Последний ингредиент должен сдвинуться")
                .isEqualTo(mockIngredientDinosaurFilling);

        softly.assertThat(burger.ingredients.contains(mockIngredientChiliSauce))
                .as("Второй ингредиент должен быть удален")
                .isFalse();

    }

    @Test
    public void moveIngredientTest() {
        burger.ingredients.add(mockIngredientHotSauce);
        burger.ingredients.add(mockIngredientDinosaurFilling);
        burger.ingredients.add(mockIngredientChiliSauce);
        burger.moveIngredient(0, 2);

        SoftAssertions softly = new SoftAssertions();

        softly.assertThat(burger.ingredients.size())
                .as("Количество ингредиентов не должно измениться")
                .isEqualTo(3);

        softly.assertThat(burger.ingredients.get(0))
                .as("Первый элемент должен стать ingredient1")
                .isEqualTo(mockIngredientDinosaurFilling);

        softly.assertThat(burger.ingredients.get(1))
                .as("Второй элемент должен стать ingredient2")
                .isEqualTo(mockIngredientChiliSauce);

        softly.assertThat(burger.ingredients.get(2))
                .as("Третий элемент должен стать ingredient0")
                .isEqualTo(mockIngredientHotSauce);

    }

    @RunWith(Parameterized.class)
    public static class BurgerPriceTest {

        private final float bunPrice;
        private final float ingredientHotSaucePrice;
        private final float ingredientDinosaurFillingPrice;
        private final float expectedPrice;

        private Burger burger;
        private Bun mockBun;
        private Ingredient mockIngredientHotSauce;
        private Ingredient mockIngredientDinosaurFilling;

        public BurgerPriceTest(float bunPrice, float ingredientHotSaucePrice, float ingredientDinosaurFillingPrice, float expectedPrice) {
            this.bunPrice = bunPrice;
            this.ingredientHotSaucePrice = ingredientHotSaucePrice;
            this.ingredientDinosaurFillingPrice = ingredientDinosaurFillingPrice;
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
            mockIngredientHotSauce = mock(Ingredient.class);
            mockIngredientDinosaurFilling = mock(Ingredient.class);

            when(mockBun.getPrice()).thenReturn(bunPrice);
            when(mockIngredientHotSauce.getPrice()).thenReturn(ingredientHotSaucePrice);
            when(mockIngredientDinosaurFilling.getPrice()).thenReturn(ingredientDinosaurFillingPrice);

            burger.bun = mockBun;
            burger.ingredients.add(mockIngredientHotSauce);
            burger.ingredients.add(mockIngredientDinosaurFilling);

            float actualPrice = burger.getPrice();

            assertEquals("Цена должна быть рассчитана правильно", expectedPrice, actualPrice, 0.01f);
        }
    }

    @Test
    public void getPriceCallsBunGetPriceOnceTest() {
        burger = new Burger();
        mockBun = mock(Bun.class);
        burger.bun = mockBun;

        burger.getPrice();

        verify(mockBun, times(1)).getPrice();
    }

    @Test
    public void getPriceCallsIngredientGetPriceOnceTest() {
        burger = new Burger();
        mockBun = mock(Bun.class);
        burger.bun = mockBun;
        mockIngredientDinosaurFilling = mock(Ingredient.class);

        burger.ingredients.add(mockIngredientDinosaurFilling);

        burger.getPrice();

        verify(mockIngredientDinosaurFilling, times(1)).getPrice();
    }

    @Test
    public void getReceiptTest() {

        burger.bun = mockBun;
        burger.ingredients.add(mockIngredientDinosaurFilling);

        Burger burgerSpy = Mockito.spy(burger);

        when(mockBun.getName()).thenReturn("white bun");
        when(mockIngredientDinosaurFilling.getType()).thenReturn(FILLING);
        when(mockIngredientDinosaurFilling.getName()).thenReturn("dinosaur");
        when(burgerSpy.getPrice()).thenReturn(500.0f);

        String actualReceipt = burgerSpy.getReceipt();

        String expectedReceipt;
        if (actualReceipt.contains("\r\n\r\nPrice:")) {
            expectedReceipt = String.format("(==== %s ====)%n", mockBun.getName()) +
                    String.format("= %s %s =%n", mockIngredientDinosaurFilling.getType().name().toLowerCase(), mockIngredientDinosaurFilling.getName()) +
                    String.format("(==== %s ====)%n", mockBun.getName()) +
                    String.format("%nPrice: %.6f%n", (mockBun.getPrice() * 2) + mockIngredientDinosaurFilling.getPrice());
        } else {
            expectedReceipt = String.format("(==== %s ====)%n", mockBun.getName()) +
                    String.format("= %s %s =%n", mockIngredientDinosaurFilling.getType().name().toLowerCase(), mockIngredientDinosaurFilling.getName()) +
                    String.format("(==== %s ====)%n", mockBun.getName()) +
                    String.format("Price: %.6f%n", (mockBun.getPrice() * 2) + mockIngredientDinosaurFilling.getPrice());
        }

        MatcherAssert.assertThat("Неверный рецепт",
                burger.getReceipt(),
                equalTo(expectedReceipt));
    }

}