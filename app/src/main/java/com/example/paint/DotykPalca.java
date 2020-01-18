package com.example.paint;

import android.graphics.Path;

public class DotykPalca {

    public int kolor;
    public boolean emboss;
    public boolean rozmycie;
    public int szerokosc;
    public Path path;

    public DotykPalca(int kolor, boolean emboss, boolean rozmycie, int szerokosc, Path path) {
        this.kolor = kolor;
        this.emboss = emboss;
        this.rozmycie = rozmycie;
        this.szerokosc = szerokosc;
        this.path = path;
    }
}
