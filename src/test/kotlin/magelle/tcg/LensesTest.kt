package magelle.tcg

import arrow.optics.*
import arrow.optics.typeclasses.At
import arrow.optics.typeclasses.Cons
import assertk.assertThat
import assertk.assertions.isEqualTo
import kotlin.test.Test

class LensesTest {

    @Test
    fun `test a simple Lens`() {
        val children = Children("Maxime")

        val updated = childrenNameLens.set(children, "Arthur")

        assertThat(childrenNameLens.get(updated)).isEqualTo("Arthur")
    }

    @Test
    fun `test a simple getter`() {
        val children = Children("Maxime")
        assertThat(childrenNameGetter.get(children)).isEqualTo("Maxime")
    }

    @Test
    fun `test a simple setter`() {
        val children = Children("Maxime")

        val updated = childrenNameSetter.set(children, "Arthur")

        assertThat(childrenNameGetter.get(updated)).isEqualTo("Arthur")
    }

    @Test
    fun `test a simple traversal`() {
        val ints = listOf(1, 2, 3)

        val traverse = Traversal.list<Int>()

        assertThat(traverse.modify(ints) { it * 10 }).isEqualTo(listOf(10, 20, 30))
    }

    @Test
    fun `test a simple Cons cons`() {
        val ints = listOf(1, 2, 3)

        val cons = Cons.list<Int>().cons()

        assertThat(cons.modify(ints) { pair: Pair<Int, List<Int>> ->
            Pair(pair.first * 10, pair.second)
        }).isEqualTo(listOf(10, 2, 3))
    }

    @Test
    fun `test a simple Cons firstOptional`() {
        val ints = listOf(1, 2, 3)

        val cons = Cons.list<Int>().firstOption()

        assertThat(cons.modify(ints) { it * 10 }).isEqualTo(listOf(10, 2, 3))
    }

    @Test
    fun `test a simple Cons tailOption`() {
        val ints = listOf(1, 2, 3)

        val cons = Cons.list<Int>().tailOption()

        assertThat(cons.modify(ints) { it.map { it * 10 } }).isEqualTo(listOf(1, 20, 30))
    }

    @Test
    fun `test uncons`() {
        val ints = listOf(1, 2, 3)

        assertThat(ints.uncons()).isEqualTo(Pair(1, listOf(2, 3)))
    }

    @Test
    fun `test cons`() {
        assertThat(1 cons listOf(2, 3)).isEqualTo(listOf(1, 2, 3))
    }


    @Test
    fun `test At`() {
        val mapAt = At.map<Int, String>().at(1)

        assertThat(mapAt.modify(
            mapOf(
                1 to "a", 2 to "b", 3 to "c"
            )
        ) { it.map { it.uppercase() } }).isEqualTo(mapOf(1 to "A", 2 to "b", 3 to "c"))
    }

    @Test
    fun `compose lenses`() {
        val firstChildren = Cons.list<Children>().firstOption()
        val firstChildName = childrens compose firstChildren compose childrenNameLens

        val mother = Mother(
            name = "Elodie",
            children = listOf(
                Children("Norah"),
                Children("Arthur")
            )
        )

        assertThat(firstChildName.getOrNull(mother))
            .isEqualTo("Norah")

        val updated = firstChildName.modifyNullable(mother) { "Nora" }!!
        assertThat(firstChildName.getOrNull(updated))
            .isEqualTo("Nora")

        val reverseChildren = childrens.lift(List<Children>::reversed)

        val updated2: Mother = reverseChildren(mother)
        assertThat(childrens.getOrNull(updated2)).isEqualTo(
            listOf(
                Children("Arthur"),
                Children("Norah")
            )
        )

        val updated3: Mother = childrens.modify(mother) { it.uncons()!!.second }
        assertThat(childrens.getOrNull(updated3)).isEqualTo(listOf(Children("Arthur")))
    }

}

data class Mother(
    val name: String, val children: List<Children>
)

val childrens: Lens<Mother, List<Children>> =
    Lens(get = { mother -> mother.children }, set = { mother, value -> mother.copy(children = value) })

val childrenNameLens: Lens<Children, String> =
    Lens(get = { children -> children.name }, set = { children, value -> children.copy(name = value) })
val childrenNameGetter = Getter(Children::name)
val childrenNameSetter = Setter<Children, String> { children, value ->
    children.copy(name = value(children.name))
}

data class Children(
    val name: String
)