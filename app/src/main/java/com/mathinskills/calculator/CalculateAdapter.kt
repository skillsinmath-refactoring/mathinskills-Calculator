package com.mathinskills.calculator

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mathinskills.calculator.data.entity.CalculationEntity
import com.mathinskills.calculator.model.SubjectItem

class CalculateAdapter(
    private val items: List<CalculationEntity>,
    private val itemClickListener: OnItemClickListener,
) :
    RecyclerView.Adapter<CalculateAdapter.SubjectViewHolder>() {
        var ItemId: Int = -1

    inner class SubjectViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        init {
            view.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    this@CalculateAdapter.setCurrentPosition(adapterPosition)
                    itemClickListener.onItemClick(items[adapterPosition])
                }
            }
        }
        val item: View = view.findViewById(R.id.item_calculate)
        val course: TextView = view.findViewById(R.id.tv_course)
        val subject: TextView = view.findViewById(R.id.tv_subject)
        val onceMinutes: TextView = view.findViewById(R.id.tv_onceMinutes)
        val weekTimes: TextView = view.findViewById(R.id.tv_weekTimes)
        val rate: TextView = view.findViewById(R.id.tv_rate)
        val standardRate: TextView = view.findViewById(R.id.tv_standardRate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubjectViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_calculate, parent, false)
        return SubjectViewHolder(view)
    }

    override fun onBindViewHolder(holder: SubjectViewHolder, position: Int) {
        val item = items[position]
        holder.course.text = item.educationOffice
        holder.subject.text = item.subject
        holder.onceMinutes.text = item.minutesPerClass.toString()
        holder.weekTimes.text = item.lessonsPerWeek.toString()
        holder.rate.text = if (item.unitPrice > item.standardPriceAtCalc) "적정" else "부적정"
        holder.item.background=if (item.unitPrice > item.standardPriceAtCalc) {
            holder.itemView.context.getDrawable(R.color.white)
        } else {
            holder.itemView.context.getDrawable(R.color.warning_color)
        }
        holder.rate.apply {
            val isLower = item.unitPrice > item.standardPriceAtCalc
            setTextColor(
                if (isLower)
                    holder.itemView.context.getColor(R.color.black)
                else
                    holder.itemView.context.getColor(R.color.text_color)
            )
            setTypeface(typeface, if (isLower) Typeface.NORMAL else Typeface.BOLD)
        }
        holder.standardRate.text = item.unitPrice.toString()
    }

    override fun getItemCount(): Int = items.size

    fun setCurrentPosition(pos: Int) {
        ItemId = pos
    }
    fun getCurrentId(): Int {
        return items[ItemId].id
    }
    fun interface OnItemClickListener {
        fun onItemClick(item: CalculationEntity)
    }
}
