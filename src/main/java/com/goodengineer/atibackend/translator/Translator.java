package com.goodengineer.atibackend.translator;

public interface Translator<A, B> {
	B translateForward(A a);

	A translateBackward(B b);
}
