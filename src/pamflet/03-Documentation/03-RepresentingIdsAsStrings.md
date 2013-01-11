Representing Ids as Strings
---------------------------

Ids are represented as strings with a Base64 like URL-safe encoding. The
encoding is based on the following index table. 

<table>
  <thead>
    <tr>
      <th>Value</th><th>Char</th><td rowspan="1">&nbsp;</td>
      <th>Value</th><th>Char</th><td rowspan="1">&nbsp;</td>
      <th>Value</th><th>Char</th><td rowspan="1">&nbsp;</td>
      <th>Value</th><th>Char</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>0</td><td>-</td><td rowspan="16">
      <td>16</td><td>F</td><td rowspan="16">
      <td>32</td><td>V</td><td rowspan="16">
      <td>48</td><td>k</td>
    </tr>
    <tr><td>1</td><td>0</td><td>17</td><td>G</td><td>33</td><td>W</td><td>49</td><td>l</td></tr>
    <tr><td>2</td><td>1</td><td>18</td><td>H</td><td>34</td><td>X</td><td>50</td><td>m</td></tr>
    <tr><td>3</td><td>2</td><td>19</td><td>I</td><td>35</td><td>Y</td><td>51</td><td>n</td></tr>
    <tr><td>4</td><td>3</td><td>20</td><td>J</td><td>36</td><td>Z</td><td>52</td><td>o</td></tr>
    <tr><td>5</td><td>4</td><td>21</td><td>K</td><td>37</td><td>_</td><td>53</td><td>p</td></tr>
    <tr><td>6</td><td>5</td><td>22</td><td>L</td><td>38</td><td>a</td><td>54</td><td>q</td></tr>
    <tr><td>7</td><td>6</td><td>23</td><td>M</td><td>39</td><td>b</td><td>55</td><td>r</td></tr>
    <tr><td>8</td><td>7</td><td>24</td><td>N</td><td>40</td><td>c</td><td>56</td><td>s</td></tr>
    <tr><td>9</td><td>8</td><td>25</td><td>O</td><td>41</td><td>d</td><td>57</td><td>t</td></tr>
    <tr><td>10</td><td>9</td><td>26</td><td>P</td><td>42</td><td>e</td><td>58</td><td>u</td></tr>
    <tr><td>11</td><td>A</td><td>27</td><td>Q</td><td>43</td><td>f</td><td>59</td><td>v</td></tr>
    <tr><td>12</td><td>B</td><td>28</td><td>R</td><td>44</td><td>g</td><td>60</td><td>w</td></tr>
    <tr><td>13</td><td>C</td><td>29</td><td>S</td><td>45</td><td>h</td><td>61</td><td>x</td></tr>
    <tr><td>14</td><td>D</td><td>30</td><td>T</td><td>46</td><td>i</td><td>62</td><td>y</td></tr>
    <tr><td>15</td><td>E</td><td>31</td><td>U</td><td>47</td><td>j</td><td>63</td><td>z</td></tr>
  </tbody>
</table>

When ids are converted to Strings they are treated as unsigned values. Apart 
from *toString*, you can use *toShortString* which creates shorter strings
omitting zeros from the beginning. [Id's companion object][1] contains a string
extractor which can be used for pattern matching.

```scala
import gr.jkl.uid.Id

val id = Id(-9217076510208286673L)

val stringId = id.toString
// stringId: String = --LMQy4R1-j

val shortStringId = id.toShortString
// shortStringId: String = LMQy4R1-j

stringId match {
  case Id(a) => println(s"Valid id: \$a")
  case _      => println("Invalid id")
}
// Valid id: --LMQy4R1-j

shortStringId match {
  case Id(a) => println(s"Valid id: \$a")
  case _      => println("Invalid id")
}
// Valid id: --LMQy4R1-j
```

**Warning**: Short string decoding is broken on versions 1.0 and 1.1.

[1]: api/latest/gr/jkl/uid/Id\$.html "gr.jkl.uid.Id\$"
