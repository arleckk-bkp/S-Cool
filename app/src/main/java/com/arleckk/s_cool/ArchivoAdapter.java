package com.arleckk.s_cool;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by arleckk on 14/07/2015.
 */
public class ArchivoAdapter extends ArrayAdapter<Archivo> {

    public ArchivoAdapter (Context context,ArrayList<Archivo> objects){
        super(context,0,objects);
    }

    public View getView(int position, View convertView, ViewGroup parent){
        LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View listItemView = convertView;
        if(null == convertView){
            listItemView = inflater.inflate(R.layout.lista_archivo,parent,false);
        }
        ImageView imgArchivo = (ImageView) listItemView.findViewById(R.id.img_archivo);
        TextView nombreArchivo = (TextView) listItemView.findViewById(R.id.text_nombre_archivo);
        TextView fechaArchivo = (TextView) listItemView.findViewById(R.id.text_fecha_archivo);
        TextView usuarioArchivo = (TextView) listItemView.findViewById(R.id.text_usuario_archivo);
        TextView urlArchivo = (TextView) listItemView.findViewById(R.id.text_url_archivo);

        Archivo item = getItem(position);
        imgArchivo.setImageDrawable(getContext().getResources().getDrawable(R.drawable.androidimg));
        nombreArchivo.setText(String.valueOf(item.getNombre_a()));
        fechaArchivo.setText(String.valueOf(item.getFecha()));
        usuarioArchivo.setText(String.valueOf(item.getAutor()));
        urlArchivo.setText(String.valueOf(item.getURL()));

        return listItemView;
    }

}
