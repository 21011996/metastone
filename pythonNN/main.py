from kerasNet import KerasNN

nn = KerasNN()

# for i in range(1000):
#     nn.learn()

for i in range(0, 10):
    s, a, r, sa = nn.dataSet[i].disolve()
    print(s)
    print(a)
    print(r)
    print(nn.classify(s))
