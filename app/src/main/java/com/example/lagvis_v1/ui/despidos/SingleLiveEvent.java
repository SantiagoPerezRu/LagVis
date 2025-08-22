package com.example.lagvis_v1.ui.despidos;

import androidx.annotation.MainThread;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import java.util.concurrent.atomic.AtomicBoolean;

public class SingleLiveEvent<T> extends MutableLiveData<T> {
    private final AtomicBoolean pending = new AtomicBoolean(false);

    @Override
    public void observe(LifecycleOwner owner, final Observer<? super T> observer) {
        super.observe(owner, t -> { if (pending.compareAndSet(true, false)) observer.onChanged(t); });
    }

    @MainThread @Override public void setValue(T t) {
        pending.set(true);
        super.setValue(t);
    }

    @MainThread public void call() { setValue(null); }
}
