package com.example.appfood.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appfood.Domain.Voucher;
import com.example.appfood.R;
import com.example.appfood.Utils.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class VoucherAdapter extends RecyclerView.Adapter<VoucherAdapter.ViewHolder> {

    private Context context;
    private List<Voucher> voucherList;
    private OnVoucherSelectListener listener;
    private int selectedPosition = -1;

    public interface OnVoucherSelectListener {
        void onVoucherSelected(Voucher voucher);
    }

    public VoucherAdapter(Context context, List<Voucher> voucherList, OnVoucherSelectListener listener) {
        this.context = context;
        this.voucherList = voucherList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VoucherAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_voucher, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VoucherAdapter.ViewHolder holder, int position) {
        Voucher voucher = voucherList.get(position);

        holder.codeText.setText(voucher.getCode());

        String type = voucher.getDiscountType();
        if (type == null || type.trim().isEmpty()) {
            holder.typeText.setText("Loại: Không rõ");
        } else if (type.equalsIgnoreCase("discount")) {
            holder.typeText.setText("Loại: Giảm giá món");
        } else if (type.equalsIgnoreCase("freeship")) {
            holder.typeText.setText("Loại: Miễn ship");
        } else {
            holder.typeText.setText("Loại: " + type);
        }

        holder.valueText.setText(Utils.formatCurrency(voucher.getValue()));
        holder.expireText.setText("HSD: " + new SimpleDateFormat("dd/MM/yyyy").format(new Date(voucher.getExpiredAt())));

        holder.selectRadio.setChecked(position == selectedPosition);

        holder.selectRadio.setOnClickListener(v -> {
            if (selectedPosition != holder.getAdapterPosition()) {
                selectedPosition = holder.getAdapterPosition();
                notifyDataSetChanged();
                if (listener != null) {
                    listener.onVoucherSelected(voucherList.get(selectedPosition));
                }
            }
        });
        holder.itemView.setOnClickListener(v -> holder.selectRadio.performClick());
    }

    @Override
    public int getItemCount() {
        return voucherList.size();
    }

    public Voucher getSelectedVoucher() {
        if (selectedPosition >= 0 && selectedPosition < voucherList.size()) {
            return voucherList.get(selectedPosition);
        }
        return null;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView codeText, typeText, valueText, expireText;
        RadioButton selectRadio;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            codeText = itemView.findViewById(R.id.voucherCodeText);
            typeText = itemView.findViewById(R.id.voucherTypeText);
            valueText = itemView.findViewById(R.id.voucherValueText);
            expireText = itemView.findViewById(R.id.voucherExpireText);
            selectRadio = itemView.findViewById(R.id.voucherSelectRadio);
        }
    }
}
