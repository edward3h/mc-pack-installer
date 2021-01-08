target "foo"

target {
    path "foo"
    include "Quack", "Honk"
}

target {
    include "Hiss"
    path "foo"
}