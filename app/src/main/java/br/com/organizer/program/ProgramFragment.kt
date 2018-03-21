package br.com.organizer.program

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.organizer.R
import br.com.organizer.model.Lecture
import br.com.organizer.model.WeakDay
import kotlinx.android.synthetic.main.card_program.view.*
import java.util.*

class ProgramFragment : Fragment() {

    companion object {

        fun newInstance(): ProgramFragment {
            val args = Bundle()
            val fragment = ProgramFragment()

            fragment.arguments = args

            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater?.inflate(R.layout.fragment_program, container, false)

        val teste = Lecture("Quimica", WeakDay.MONDAY, Date(), Date())

        val dataSet = arrayListOf<TestModel>(TestModel("1"),
                TestModel("2"),
                TestModel("3"),
                TestModel("4"),
                TestModel("5"),
                TestModel("6"),
                TestModel("7"),
                TestModel("8"),
                TestModel("9"),
                TestModel("10"),
                TestModel("11"),
                TestModel("12"),
                TestModel("13"),
                TestModel("14"),
                TestModel("15"),
                TestModel("16"),
                TestModel("17"),
                TestModel("18"),
                TestModel("19"),
                TestModel("20"),
                TestModel("21"),
                TestModel("22"),
                TestModel("23"))


        val managerSpan = GridLayoutManager(activity, 4, GridLayoutManager.VERTICAL, false)
        managerSpan.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {

                if (position == 4 || position == 12 || position == 19)
                    return 4

                return 1
            }

        }


        val recycler = view?.findViewById<RecyclerView>(R.id.card_recycler)!!
        val layoutManager = StaggeredGridLayoutManager(5, StaggeredGridLayoutManager.VERTICAL)

        val adapter = TestAdapter(dataSet, activity)



        recycler.layoutManager = managerSpan
        recycler.adapter = adapter

        return view
    }


    class TestAdapter constructor(var dataSet: List<TestModel>, var context: Context) : RecyclerView.Adapter<TestAdapter.Companion.TestViewHolder>() {
        companion object {

            class TestViewHolder constructor(v: View?) : RecyclerView.ViewHolder(v) {
                fun bindData(title: String) {
                    itemView.card_title.text = title
                }
            }

        }

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): TestViewHolder {
            val v = LayoutInflater.from(context).inflate(R.layout.card_program, parent, false)

            return TestViewHolder(v)
        }

        override fun onBindViewHolder(holder: TestViewHolder?, position: Int) {
            holder?.bindData(dataSet[position].name)

        }

        override fun getItemCount() = dataSet.size


    }

    data class TestModel constructor(var name: String)
}

