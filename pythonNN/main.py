from kerasNet import KerasNN
import time


nn = KerasNN()
t0 = time.time()
for i in range(10000):
    nn.learn()
t1 = time.time()

total = t1-t0
print(total)
